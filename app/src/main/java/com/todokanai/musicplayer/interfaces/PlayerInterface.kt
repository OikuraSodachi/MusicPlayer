package com.todokanai.musicplayer.interfaces

import com.todokanai.musicplayer.data.room.Music

interface PlayerInterface {

    fun onStart(isPlaying:Boolean)

    fun onPause(isPlaying:Boolean)

    fun onStop(isPlaying: Boolean)

    fun setLooping(isLooping:Boolean)

    fun setCurrentMusic(music:Music)

    fun toggleShuffle()

}