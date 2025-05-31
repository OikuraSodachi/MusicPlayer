package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music

interface PlayerInterface {

//    fun onStart(isPlaying:Boolean)
//
//    fun onPause(isPlaying:Boolean)
//
//    fun onStop(isPlaying: Boolean)
//
//    fun setLooping(isLooping:Boolean)
//
//    fun setCurrentMusic(music:Music)
//
//    fun toggleShuffle()

    fun repeatAction()

    fun pausePlayAction()

    fun launchMusic(context: Context, music: Music)

    fun toNextMusic(context: Context)

    fun toPrevMusic(context: Context)

    fun toggleShuffle()

}