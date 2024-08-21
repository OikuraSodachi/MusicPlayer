package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders @Inject constructor( dsRepo:DataStoreRepository, musicRepo:MusicRepository) {

    val currentMusicHolder = musicRepo.currentMusic.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = dummyMusic
    )

    val isLoopingHolder = dsRepo.isLooping.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = false
    )

    val isShuffledHolder = dsRepo.isShuffled.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = false
    )

    val seedHolder = dsRepo.seed.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = 0.0
    )
}