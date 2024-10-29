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
import kotlin.properties.Delegates
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
    dummyMusic: Music = MyObjects.dummyMusic
) :MediaInterface{

    companion object{
        var initialSeed by Delegates.notNull<Double>()
        lateinit var initialPlayList:Array<Music>
        var initialMusic : Music? = null
        var initialShuffle by Delegates.notNull<Boolean>()
        var initialLoop by Delegates.notNull<Boolean>()
    }

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
            dsRepo.saveSeed(seed)
        }
    }

    private val _playListHolder = MutableStateFlow<Array<Music>>(initialPlayList)

    override val playListHolder =
        combine(
            _playListHolder,
            isShuffledHolder,
            seedHolder
        ){ musics ,shuffled,seed ->
            modifiedPlayList(musics,shuffled,seed)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = modifiedPlayList(initialPlayList,initialShuffle,initialSeed)
        )



    fun updatePlayList(newList:Array<Music>){
        this._playListHolder.value = newList

        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.updateMusicList(newList)
        }
    }


    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        println("seed: $seed")
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}