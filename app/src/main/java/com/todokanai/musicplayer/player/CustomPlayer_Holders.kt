package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class CustomPlayer_Holders (
    private val dsRepo:DataStoreRepository,
    private val musicRepo:MusicRepository
){

    private val initialLoop = false
    private val initialShuffle = false
    private val initialSeed : Double = 0.1
    private val initialMusicArray = emptyArray<Music>()
    private val initialMusic :Music = dummyMusic

    fun saveShuffle(isShuffled:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    fun saveLoop(isLooping:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    fun saveSeed(seed:Double){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveSeed(seed)
        }
    }

    val _isShuffledHolder = dsRepo.isShuffled.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialShuffle
    )

    val _isLoopingHolder = dsRepo.isLooping.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialLoop
    )

    val _seedHolder = dsRepo.seed.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialSeed
    )

    val musicArrayHolder = musicRepo.getAll.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialMusicArray
    )

    val _currentMusicHolder = musicRepo.currentMusic.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialMusic
    )
}