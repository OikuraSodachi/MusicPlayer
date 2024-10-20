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
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
    initialSeed:Double = 0.0,
    initialPlayList:List<Music> = emptyList(),
    initialLoop:Boolean = false,
    initialShuffle:Boolean = false,
    dummyMusic: Music = MyObjects.dummyMusic
) :MediaInterface{
    private val _isPlayingHolder_new = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = _isPlayingHolder_new

    fun setIsPlaying(isPlaying:Boolean){
        _isPlayingHolder_new.value = isPlaying
    }

    private val _currentMusicHolder_new = MutableStateFlow<Music>(dummyMusic)
    override val currentMusicHolder : StateFlow<Music>
        get() = _currentMusicHolder_new

    fun setCurrentMusic(music: Music){
        _currentMusicHolder_new.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    private val _isLoopingHolder_new = MutableStateFlow<Boolean>(initialLoop)
    override val isLoopingHolder : StateFlow<Boolean>
        get() = _isLoopingHolder_new

    fun setIsLooping(isLooping:Boolean){
        _isLoopingHolder_new.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private val _isShuffledHolder_new = MutableStateFlow<Boolean>(initialShuffle)
    override val isShuffledHolder : StateFlow<Boolean>
        get() = _isShuffledHolder_new

    fun setShuffle(isShuffled:Boolean){
        _isShuffledHolder_new.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private val _seedHolder_new = MutableStateFlow<Double>(initialSeed)
    override val seedHolder : StateFlow<Double>
        get() = _seedHolder_new

    fun setSeed(seed: Double){
        _seedHolder_new.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveRandomSeed(seed)
        }
    }

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