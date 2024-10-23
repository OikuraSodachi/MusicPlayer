package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterface
import com.todokanai.musicplayer.myobjects.MyObjects
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    val musicRepo:MusicRepository,
    initialSeed:Double = 0.0,
    initialPlayList:List<Music>,
    initialLoop:Boolean,
    initialShuffle:Boolean,
    dummyMusic: Music = MyObjects.dummyMusic
) :MediaInterface{

    @Inject
    lateinit var dsRepo: DataStoreRepository

    private val isPlaying = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = isPlaying

    fun setIsPlaying(isPlaying:Boolean){
        this.isPlaying.value = isPlaying
    }

    private val currentMusic = MutableStateFlow<Music>(dummyMusic)
    override val currentMusicHolder : StateFlow<Music>
        get() = currentMusic

    fun setCurrentMusic(music: Music){
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

    /** Todo: MusicRepo.getAll (Room을 observe) 대신 musicListHolder( Array<Music>)를 사용하도록 변경할것  **/
    override val playListHolder =
        combine(
            musicRepo.getAll,
            isShuffledHolder,
            seedHolder
        ){ musics ,shuffled,seed ->
            modifiedPlayList(musics.sortedBy{it.title},shuffled,seed)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = modifiedPlayList(initialPlayList,initialShuffle,initialSeed)
        )

    private fun modifiedPlayList(musicList:List<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

    fun <Type:Any> MutableStateFlow<Type>.setValue_td(value:Type,save:(value:Type)->Unit){
        this.value = value
        CoroutineScope(Dispatchers.IO).launch {
            save(value)
        }
    }
}