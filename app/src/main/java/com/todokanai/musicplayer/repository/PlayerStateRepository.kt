package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.player.NewPlayer
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStateRepository @Inject constructor(
    val player: NewPlayer,
    val dsRepo: DataStoreRepository
) {

    val isShuffledHolder = dsRepo.isShuffled

    val musicStateFlow by lazy {
        combine(
            player.isPlayingHolder,
            player.isLoopingHolder,
            isShuffledHolder,
            player.currentMusicHolder
        ) { isPlaying, isLooping, isShuffled, currentMusic ->
            MusicState(
                isPlaying = isPlaying,
                isLooping = isLooping,
                isShuffled = isShuffled,
                currentMusic = currentMusic
            )
        }
    }

}

data class MusicState(
    val isPlaying:Boolean = false,
    val isLooping:Boolean = false,
    val isShuffled:Boolean = false,
    val currentMusic: Music = dummyMusic
)