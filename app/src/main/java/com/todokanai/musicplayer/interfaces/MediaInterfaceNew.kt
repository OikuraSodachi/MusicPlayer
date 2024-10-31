package com.todokanai.musicplayer.interfaces

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

interface MediaInterfaceNew {

    val mediaPlayer: MediaPlayer
    val isPlayingHolder: StateFlow<Boolean>
    val currentMusicHolder: StateFlow<Music>
    val isLoopingHolder: StateFlow<Boolean>
    val isShuffledHolder: StateFlow<Boolean>
}