package com.todokanai.musicplayer.interfaces

import com.todokanai.musicplayer.data.room.Music

interface MediaInterfaceNew {
    fun setMusic(music: Music)

    fun repeatAction()

    fun prevAction()

    fun pausePlayAction()

    fun nextAction()

    fun shuffleAction()
}