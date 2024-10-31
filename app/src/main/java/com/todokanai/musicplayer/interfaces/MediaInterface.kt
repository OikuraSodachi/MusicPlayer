package com.todokanai.musicplayer.interfaces

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

/** for View Layer **/
interface MediaInterface {
    val mediaPlayer:MediaPlayer
    val isPlayingHolder: StateFlow<Boolean>
    val seedHolder:StateFlow<Double>
    val currentMusicHolder:StateFlow<Music>
    val isLoopingHolder:StateFlow<Boolean>
    val isShuffledHolder:StateFlow<Boolean>

}