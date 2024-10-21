package com.todokanai.musicplayer.myobjects

import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.components.receiver.NoisyReceiver

object LateInitObjects {

    /** initiated in MusicService.kt **/
    lateinit var receiver: MusicReceiver
    lateinit var noisyReceiver: NoisyReceiver
}