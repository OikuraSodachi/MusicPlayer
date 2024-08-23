package com.todokanai.musicplayer.myobjects

import com.todokanai.musicplayer.components.receiver.MusicReceiver

object LateInitObjects {

    /** initiated in MusicService.kt **/
    lateinit var receiver: MusicReceiver
}