package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterface
import com.todokanai.musicplayer.myobjects.MyObjects
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
    dummyMusic: Music = MyObjects.dummyMusic
) :MediaInterface{

    private val initialSeed : Double = 0.1
    private var musicArray = emptyArray<Music>()
    private val initialMusic = dummyMusic
    private val initialShuffle = false
    private val initialLoop = false

    override val mediaPlayer = MediaPlayer()

    private val isPlaying = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = isPlaying

    fun setIsPlaying(isPlaying:Boolean){
        this.isPlaying.value = isPlaying
    }

    private val currentMusic = MutableStateFlow<Music>(initialMusic)
    override val currentMusicHolder : StateFlow<Music>
        get() = currentMusic

    private fun setCurrentMusic(music: Music){
        currentMusic.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    private val isLooping = MutableStateFlow<Boolean>(initialLoop)
    override val isLoopingHolder : StateFlow<Boolean>
        get() = isLooping

    fun setIsLooping(isLooping:Boolean){
        this.isLooping.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private val isShuffled = MutableStateFlow<Boolean>(initialShuffle)
    override val isShuffledHolder : StateFlow<Boolean>
        get() = isShuffled

    fun setShuffle(isShuffled:Boolean){
        this.isShuffled.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private val seed = MutableStateFlow<Double>(initialSeed)
    override val seedHolder : StateFlow<Double>
        get() = seed

    fun setSeed(seed: Double){
        this.seed.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveRandomSeed(seed)
        }
    }

    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

    fun playList():List<Music>{
        return modifiedPlayList(musicArray,isShuffledHolder.value,seedHolder.value)
    }

    /** onCompletion() 의 context에 주의 (?). 아직 미검증 상태  **/
    private fun setMusicPrimitive(music: Music,context: Context){
        val shouldLoop = mediaPlayer.isLooping
        val isMusicValid = music.fileDir != "empty"

        if (isMusicValid) {
            mediaPlayer.reset()
            mediaPlayer.apply {
                setDataSource(context, music.getUri())
                setOnCompletionListener {
                    if (!isLooping) {
                        context.sendBroadcast(nextIntent)
                    }
                }
                isLooping = shouldLoop
                prepare()
            }
            setCurrentMusic(music)
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
                    getCircularNext_td(playList, playList.indexOf(targetMusic)),    // try on the next item
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

    suspend fun onInitAttributes(context: Context){
        setSeed(dsRepo.getSeed())
        setShuffle(dsRepo.isShuffled() ?: false)
        setIsLooping(dsRepo.isLooping() ?: false)
        musicArray = musicRepo.getAllNonFlow()
        // init values
        setMusic(context,musicRepo.currentMusicNonFlow())
    }
}