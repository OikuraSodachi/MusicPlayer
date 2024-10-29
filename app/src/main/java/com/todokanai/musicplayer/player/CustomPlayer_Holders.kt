package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface CustomPlayer_Holders
   // private val dsRepo:DataStoreRepository,
//    private val musicRepo:MusicRepository
{
    val dsRepo:DataStoreRepository
    val musicRepo:MusicRepository

    var isShuffled:Boolean

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
}