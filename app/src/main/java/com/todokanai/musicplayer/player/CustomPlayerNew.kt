package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class CustomPlayerNew(val dataStoreRepository:DataStoreRepository) {

    private val scope = CoroutineScope(Dispatchers.Default)
    val mediaPlayer = MediaPlayer()

    private val initialLoop = false
    private val initialShuffle = false
    private val initialSeed : Double = 0.1

    val isShuffledHolder: StateFlow<Boolean> =
        dataStoreRepository.isShuffled.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialShuffle
        )
    val isLoopingHolder: StateFlow<Boolean> =
        dataStoreRepository.isShuffled.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialLoop
        )

    val seedHolder : StateFlow<Double> =
        dataStoreRepository.seed.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialSeed
        )

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder :StateFlow<Boolean>
        get() = _isPlayingHolder

    fun isShuffled():Boolean{
        return isShuffledHolder.value
    }

    abstract fun setMusic(music: Music)

    abstract fun repeatAction()

    abstract fun prevAction()

    abstract fun pausePlayAction()

    abstract fun nextAction()

    abstract fun shuffleAction()

    fun setMusic(music: Music,context: Context){
        val isMusicValid = music.fileDir != "empty"
        if (isMusicValid) {
            mediaPlayer.apply {
                reset()
                setDataSource(context, music.getUri())
                setOnCompletionListener {
                    if (!isLooping) {
                        context.sendBroadcast(nextIntent)
                    }
                }
                prepare()
            }
        }
    }


    suspend fun isShuffled_Setter(isShuffled:Boolean){
        dataStoreRepository.saveIsShuffled(isShuffled)
    }

    suspend fun isLooping_Setter(isLooping:Boolean){
        dataStoreRepository.saveIsLooping(isLooping)
    }

    suspend fun seed_Setter(seed:Double){
        dataStoreRepository.saveRandomSeed(seed)
    }

    fun isPlaying_Setter(isPlaying:Boolean){
        _isPlayingHolder.value = isPlaying
    }
}