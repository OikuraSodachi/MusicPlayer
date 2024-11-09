package com.todokanai.musicplayer.interfaces

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random


/** latest value 저장 작업은 여기서 전부 처리함 **/
abstract class MediaInterfaceNew(
    val dataStoreRepository:DataStoreRepository,
    val musicRepository:MusicRepository
):MediaPlayer() {

    //var musicArray = emptyArray<Music>()

    /** 재생 목록의 전체 Array **/
    private val musicArrayFlow = musicRepository.getAll.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = emptyArray()
    )

    private val initialSeed : Double = 0.1
    private val initialMusic = dummyMusic
    private val initialShuffle = false
    private val initialLoop = false

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    /** for view layer Only **/
    val isPlayingHolder : StateFlow<Boolean>
        get() = _isPlayingHolder

    private val _seedHolder = MutableStateFlow<Double>(initialSeed)
    val seedHolder : StateFlow<Double>
        get() = _seedHolder

    private val _currentMusicHolder = MutableStateFlow<Music>(initialMusic)
    val currentMusicHolder : StateFlow<Music>
        get() = _currentMusicHolder

    private val _isLoopingHolder = MutableStateFlow<Boolean>(initialLoop)
    /** for view layer Only **/
    val isLoopingHolder : StateFlow<Boolean>
        get() = _isLoopingHolder

    private val _isShuffledHolder = MutableStateFlow<Boolean>(initialShuffle)
    /** for view layer Only **/
    val isShuffledHolder : StateFlow<Boolean>
        get() = _isShuffledHolder


    /** setter for isLoopingHolder **/
    override fun setLooping(isLooping: Boolean) {
        super.setLooping(isLooping)
        _isLoopingHolder.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.saveIsLooping(isLooping)
        }
    }

    fun isShuffled():Boolean{
        return _isShuffledHolder.value
    }


    /** setter for isShuffledHolder **/
    fun setShuffle(shuffled:Boolean){
        _isShuffledHolder.value = shuffled
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.saveIsShuffled(shuffled)
        }
    }

    fun currentMusic(): Music{
        return _currentMusicHolder.value
    }

    /** setter for currentMusicHolder **/
    private fun setCurrentMusic(music: Music){
        _currentMusicHolder.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.upsertCurrentMusic(music)
        }
    }

    private fun setSeed(seed:Double){
        _seedHolder.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.saveRandomSeed(seed)
        }
    }

    /** setter for isPlayingHolder **/
    private fun setPlayingHolder(){
        _isPlayingHolder.value = isPlaying
    }

    override fun start() {
        super.start()
        setPlayingHolder()
    }

    override fun pause() {
        super.pause()
        setPlayingHolder()
    }

    override fun reset(){
        super.reset()
        setPlayingHolder()
    }

    override fun stop() {
        super.stop()
        setPlayingHolder()
    }
    abstract fun initAttributes()

    abstract fun playList():List<Music>

    val playListFlow = combine(
        musicRepository.getAll,
        isShuffledHolder,
        seedHolder
    ){ musics,shuffled,seed ->
        modifiedPlayList(musics, shuffled, seed)
    }

    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

    /** includes setter for currentMusicHolder **/
    fun setMusic(
        context: Context,
        targetMusic: Music,
    ){
        setMusic_generic(
            context = context,
            targetMusic = targetMusic,
            playList = playList(),
            setMusicPrimitive = {setMusicPrimitive(it,context)},
            onFailure = {
                Toast.makeText(context,context.getString(R.string.all_item_failure), Toast.LENGTH_SHORT).show()
            },
            onListEmpty = {
                Toast.makeText(context,context.getString(R.string.playList_empty), Toast.LENGTH_SHORT).show()
            }
        )
    }


    /** onCompletion() 의 context에 주의 (?). 아직 미검증 상태  **/
    private fun setMusicPrimitive(music: Music,context: Context){
        val shouldLoop = isLooping
        val isMusicValid = music.fileDir != "empty"

        if (isMusicValid) {
            reset()
         //   apply {
                setDataSource(context, music.getUri())
                setOnCompletionListener {
                    if (!isLooping) {
                        context.sendBroadcast(nextIntent)
                    }
                }
                isLooping = shouldLoop
                prepare()
          //  }
            setCurrentMusic(music)
        }
    }


    /** targetMusic에 대해서 setMusic. 실패시 다음 music에 시도  (recursive) **/
    private fun setMusic_generic(
        context: Context,
        targetMusic:Music,
        playList:List<Music>,
        setMusicPrimitive:(Music)->Unit,
        trialCount:Int = 1,
        onFailure:()->Unit,
        onListEmpty:()->Unit
    ):Unit{
        try{
            if(playList.isNotEmpty()) {
                setMusicPrimitive(targetMusic)
            }else{
                onListEmpty()
            }
        }catch (e:Exception){
            e.printStackTrace()
            if(playList.size != trialCount) {
                setMusic_generic(
                    context,
                    getCircularNext_td(playList, targetMusic),    // try on the next item
                    playList,
                    setMusicPrimitive,
                    trialCount+1,
                    onFailure,
                    onListEmpty
                )
            }else{
                onFailure()     // failure on all items
            }
        }
    }

}