package com.todokanai.musicplayer.interfaces

import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

/** for View Layer **/
interface MediaInterface {

    val isPlayingHolder: StateFlow<Boolean>
    val seedHolder:StateFlow<Double>
    val currentMusicHolder:StateFlow<Music>
    val isLoopingHolder:StateFlow<Boolean>
    val isShuffledHolder:StateFlow<Boolean>
    val playListHolder:StateFlow<List<Music>>

}