package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random


/** Dummy Data **/
///** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository,
    initialSeed:Double,
    initialPlayList:List<Music>,
    initialLoop:Boolean,
    initialShuffle:Boolean
) {

    val currentMusicHolder = musicRepo.currentMusic.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = dummyMusic
    )

    val isLoopingHolder = dsRepo.isLooping.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialLoop
    )

    val isShuffledHolder = dsRepo.isShuffled.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialShuffle
    )

    val seedHolder = dsRepo.seed.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialSeed
    )
    val musicsHolder = musicRepo.getAll

    val playListHolder =
        combine(
            musicRepo.getAll,
            dsRepo.isShuffled,
            dsRepo.seed
        ){ musics ,shuffled,seed ->
            modifiedPlayList(musics.sortedBy{it.title},shuffled,seed)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = modifiedPlayList(initialPlayList,initialShuffle,initialSeed)
        )

    private fun modifiedPlayList(musicList:List<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

}