package com.todokanai.musicplayer.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.player.NewPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MusicState(
                isPlaying = false,
                isLooping = false,
                isShuffled = false,
                currentMusic = dummyMusic
            )
        )
    }

    fun activate(context: Context){
        musicStateFlow.asLiveData().observeForever{

        }
    }

}

data class MusicState(
    val isPlaying:Boolean,
    val isLooping:Boolean,
    val isShuffled:Boolean,
    val currentMusic: Music
)