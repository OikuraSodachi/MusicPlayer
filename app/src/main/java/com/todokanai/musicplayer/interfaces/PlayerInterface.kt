package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music

interface PlayerInterface {

    fun launchMusic(context: Context, music: Music)

    fun repeatAction(context: Context)

    fun pausePlayAction(context: Context)
}