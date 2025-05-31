package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music

interface PlayerInterface {

    fun repeatAction()

    fun pausePlayAction()

    fun launchMusic(context: Context, music: Music)

    fun toNextMusic(context: Context)

    fun toPrevMusic(context: Context)

    fun toggleShuffle()

}