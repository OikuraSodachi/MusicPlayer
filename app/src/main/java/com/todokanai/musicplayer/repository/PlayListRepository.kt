package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayListRepository @Inject constructor(val dsRepo:DataStoreRepository, musicRepo:MusicRepository) {

    val playList = combine(
        musicRepo.getAll,
        dsRepo.isShuffled,
        dsRepo.seed
    ){ musicArray, isShuffled, seed ->
        val musicList = musicArray.sortedBy{it.title}
        if(isShuffled){
            musicList.shuffled(Random(seed))
        }else{
            musicList.sortedBy{it.title}
        }
    }

    /** flow of prev, current, next music **/
    val musicCache = combine(
        playList,
        musicRepo.currentMusic
    ){ playList, currentMusic ->
        val prevMusic = getCircularPrev_td(playList,currentMusic)
        val nextMusic = getCircularNext_td(playList,currentMusic)
        Triple(prevMusic,currentMusic,nextMusic)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = Triple(dummyMusic,dummyMusic,dummyMusic)
    )

    suspend fun toggleShuffle() = dsRepo.saveIsShuffled(!dsRepo.isShuffled())

    fun prevMusic() = musicCache.value.first
    fun nextMusic() = musicCache.value.third

}