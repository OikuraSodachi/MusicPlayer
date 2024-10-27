package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CustomPlayer_R(
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
    val scope:CoroutineScope
):CustomPlayerNew(dsRepo) {

    private val initialLoop = false
    private val initialShuffle = false

    override val isShuffledHolder: StateFlow<Boolean>
        get() = dataStoreRepository.isShuffled.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialShuffle
        )
    override val isLoopingHolder: StateFlow<Boolean>
        get() = dataStoreRepository.isShuffled.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialLoop
        )

    override fun isShuffled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setMusic(music: Music) {
        TODO("Not yet implemented")
    }

    override fun repeatAction() {
        TODO("Not yet implemented")
    }

    override fun prevAction() {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction() {
        TODO("Not yet implemented")
    }

    override fun nextAction() {
        TODO("Not yet implemented")
    }

    override fun shuffleAction() {
        TODO("Not yet implemented")
    }
}