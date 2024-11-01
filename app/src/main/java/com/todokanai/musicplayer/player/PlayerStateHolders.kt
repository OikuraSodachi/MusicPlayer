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

    private val initialSeedNew : Double = 0.1
    private var musicArray = emptyArray<Music>()
    private val initialMusicNew = dummyMusic
    private val initialShuffleNew = false
    private val initialLoopNew = false

    override val mediaPlayer = MediaPlayer()

    private val isPlaying = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = isPlaying

    fun setIsPlaying(isPlaying:Boolean){
        this.isPlaying.value = isPlaying
    }

    private val currentMusic = MutableStateFlow<Music>(initialMusicNew)
    override val currentMusicHolder : StateFlow<Music>
        get() = currentMusic

    private fun setCurrentMusic(music: Music){
        currentMusic.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    private val isLooping = MutableStateFlow<Boolean>(initialLoopNew)
    override val isLoopingHolder : StateFlow<Boolean>
        get() = isLooping

    fun setIsLooping(isLooping:Boolean){
        this.isLooping.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private val isShuffled = MutableStateFlow<Boolean>(initialShuffleNew)
    override val isShuffledHolder : StateFlow<Boolean>
        get() = isShuffled

    fun setShuffle(isShuffled:Boolean){
        this.isShuffled.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private val seed = MutableStateFlow<Double>(initialSeedNew)
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
    private fun setMusicPrimitive(music: Music?,context: Context){
        music?.let {
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
    }

    /** includes setter for currentMusicHolder **/
    fun setMusic(music: Music?,context: Context){
        val playList = playList()
        var i = 0
        try {
            setMusicPrimitive(
                music = music,
                context = context
            )
        } catch(e:Exception){
            println(e.stackTrace)
            i++
            if(i!=playList.size) {
                setMusicPrimitive(
                    music = getCircularNext_td(playList, playList.indexOf(music)),
                    context = context
                )
            } else{
                Toast.makeText(context,context.getString(R.string.all_item_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun onInitAttributes(context: Context){
        setSeed(dsRepo.getSeed())
        setShuffle(dsRepo.isShuffled())
        setIsLooping(dsRepo.isLooping())
        musicArray = musicRepo.getAllNonFlow()
        // init values
        setMusic(musicRepo.currentMusicNonFlow(),context)
    }
}