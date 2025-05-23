package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music

abstract class BasePlayer(val mediaPlayer: MediaPlayer) {

    abstract fun play()
    abstract fun pause()

    abstract fun updateViewLayer(isPlaying:Boolean,isLooping:Boolean,isShuffled:Boolean,currentMusic: Music)

}