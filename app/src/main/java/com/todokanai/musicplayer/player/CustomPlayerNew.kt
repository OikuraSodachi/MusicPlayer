package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class CustomPlayerNew(val dataStoreRepository:DataStoreRepository) {

    val mediaPlayer = MediaPlayer()

    abstract val isShuffledHolder: StateFlow<Boolean>

    abstract val isLoopingHolder: StateFlow<Boolean>

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder :StateFlow<Boolean>
        get() = _isPlayingHolder

    abstract fun isShuffled():Boolean

    abstract fun setMusic(music: Music)

    abstract fun repeatAction()

    abstract fun prevAction()

    abstract fun pausePlayAction()

    abstract fun nextAction()

    abstract fun shuffleAction()

    suspend fun isShuffled_Setter(isShuffled:Boolean){
        dataStoreRepository.saveIsShuffled(isShuffled)
    }

    suspend fun isLooping_Setter(isLooping:Boolean){
        dataStoreRepository.saveIsLooping(isLooping)
    }

    fun isPlaying_Setter(isPlaying:Boolean){
        _isPlayingHolder.value = isPlaying
    }



}