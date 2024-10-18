package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerStateObserver
import kotlinx.coroutines.flow.StateFlow

class MyPlayerStateObserver: PlayerStateObserver() {
    override val mediaPlayer: MediaPlayer
        get() = TODO("Not yet implemented")

    override fun isPlayingHolder(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun isLoopingHolder(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun isShuffledHolder(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun currentMusicHolder(): StateFlow<Music> {
        TODO("Not yet implemented")
    }
}