package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PlayerAddOn @Inject constructor(player: CustomPlayer):MediaInterfaceNew {
    override val mediaPlayer: MediaPlayer = player.mediaPlayer
    override val isPlayingHolder: StateFlow<Boolean> = player.isPlayingHolder
    override val currentMusicHolder: StateFlow<Music> = player.currentMusicHolder
    override val isLoopingHolder: StateFlow<Boolean> = player.isLoopingHolder
    override val isShuffledHolder: StateFlow<Boolean> = player.isShuffledHolder
}