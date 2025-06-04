package com.todokanai.musicplayer.interfaces

import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.player.MusicState
import kotlinx.coroutines.flow.Flow

interface PlayerStateInterface {

    val musicStateFlow: Flow<MusicState>

    val playList: Flow<List<Music>>
}