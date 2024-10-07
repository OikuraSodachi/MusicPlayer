package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    musicRepo:MusicRepository,
    initialSeed:Double,
    initialPlayList:List<Music>,
    initialLoop:Boolean,
    initialShuffle:Boolean,
    dummyMusic: Music
) {

    val currentMusicHolder_new = MutableStateFlow<Music>(dummyMusic)
    val isLoopingHolder_new = MutableStateFlow<Boolean>(initialLoop)
    val isShuffledHolder_new = MutableStateFlow<Boolean>(initialShuffle)
    val seedHolder_new = MutableStateFlow<Double>(initialSeed)

    val playListHolder =
        combine(
            musicRepo.getAll,
            isShuffledHolder_new,
            seedHolder_new
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

    //--------------


}