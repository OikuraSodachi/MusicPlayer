package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

abstract class BasePlayer(val mediaPlayer: MediaPlayer, val mediaSession: MediaSessionCompat) {

    abstract val isPlayingFlow:StateFlow<Boolean>
    abstract val isLoopingFlow: StateFlow<Boolean>
    abstract val isShuffledFlow: StateFlow<Boolean>
    abstract val currentMusicFlow: StateFlow<Music>
    val musicStateFlow by lazy {
        combine(
            isPlayingFlow,
            isLoopingFlow,
            isShuffledFlow,
            currentMusicFlow
        ) { isPlaying, isLooping, isShuffled, currentMusic ->
            MusicState(
                isPlaying = isPlaying,
                isLooping = isLooping,
                isShuffled = isShuffled,
                currentMusic = currentMusic
            )
        }
    }

    fun activate(context: Context){
        musicStateFlow.asLiveData().observeForever{
            updateViewLayer(
                isPlaying = it.isPlaying,
                isLooping = it.isLooping,
                isShuffled = it.isShuffled,
                currentMusic = it.currentMusic
            )
        }
    }


    abstract fun setLooping(isLooping: Boolean)
    abstract fun setShuffled(isShuffled: Boolean)
    abstract fun play()
    abstract fun pause()

    fun launchMusic(context: Context,music: Music){
        mediaPlayer.run{
            setMusic_td(context, music)
            start()
        }
    }

    abstract fun updateViewLayer(isPlaying:Boolean,isLooping:Boolean,isShuffled:Boolean,currentMusic: Music)

    private fun MediaPlayer.setMusic_td(context: Context, music: Music){
        val isMusicValid = music.fileDir != "empty"

        if (isMusicValid) {
            this.run {
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
}
data class MusicState(
    val isPlaying:Boolean,
    val isLooping:Boolean,
    val isShuffled:Boolean,
    val currentMusic: Music
)