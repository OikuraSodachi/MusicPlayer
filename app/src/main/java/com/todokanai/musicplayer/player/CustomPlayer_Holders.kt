package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.random.Random

interface CustomPlayer_Holders {
    val dsRepo:DataStoreRepository
    val musicRepo:MusicRepository

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

  //  /*
    val isShuffledHolder: Flow<Boolean>
        get() = dsRepo.isShuffled
    val isLoopingHolder: Flow<Boolean>
        get() = dsRepo.isLooping
    val seedHolder: Flow<Double>
        get() = dsRepo.seed

    val musicArrayHolder : Flow<Array<Music>>
        get() = musicRepo.getAll

    val currentMusicHolder: Flow<Music?>
        get() = musicRepo.currentMusic


    val playListHolder :Flow<List<Music>>
    // */
    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}