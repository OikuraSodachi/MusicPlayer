package com.todokanai.musicplayer.myobjects

import com.todokanai.musicplayer.components.receiver.MusicReceiver

object LateInitObjects {
    /*
    /** initiated in MainViewModel.kt **/
    lateinit var customPlayer: CustomPlayer
     */

    /*
    /** initiated in MainViewModel.kt **/
    lateinit var mediaSession: MyMediaSession

     */

    /** initiated in MusicService.kt **/
    lateinit var receiver: MusicReceiver
}