package com.todokanai.musicplayer.myobjects

import com.todokanai.musicplayer.components.service.MusicService

/** Refactoring 편의를 위한 getter 모음 **/
object Getters {
    val getPlayer
        get() = MusicService.customPlayer
}