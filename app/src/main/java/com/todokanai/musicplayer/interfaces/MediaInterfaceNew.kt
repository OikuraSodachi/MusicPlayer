package com.todokanai.musicplayer.interfaces

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/** latest value 저장 작업은 여기서 전부 처리함 **/
abstract class MediaInterfaceNew(
    val dataStoreRepository:DataStoreRepository,
    val musicRepository:MusicRepository
):MediaPlayer() {

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder : StateFlow<Boolean>
        get() = _isPlayingHolder

    private val _seedHolder = MutableStateFlow<Double>(0.1)
    val seedHolder : StateFlow<Double>
        get() = _seedHolder

    private val _currentMusicHolder = MutableStateFlow<Music>(dummyMusic)
    val currentMusicHolder : StateFlow<Music>
        get() = _currentMusicHolder

    private val _isLoopingHolder = MutableStateFlow<Boolean>(false)
    val isLoopingHolder : StateFlow<Boolean>
        get() = _isLoopingHolder

    private val _isShuffledHolder = MutableStateFlow<Boolean>(false)
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
    fun setCurrentMusic(music: Music){
        _currentMusicHolder.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.upsertCurrentMusic(music)
        }
    }

    fun setSeed(seed:Double){
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


}