package com.todokanai.musicplayer.interfaces

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

abstract class PlayerStateObserver {

    abstract val mediaPlayer: MediaPlayer

    abstract fun isPlayingHolder():StateFlow<Boolean>

    abstract fun isLoopingHolder():StateFlow<Boolean>

    abstract fun isShuffledHolder():StateFlow<Boolean>

    abstract fun currentMusicHolder():StateFlow<Music>
}