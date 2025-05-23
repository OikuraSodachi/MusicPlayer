package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Singleton

@Singleton
class PlayListRepository( dsRepo:DataStoreRepository,musicRepo:MusicRepository) {

    val playList = combine(
        musicRepo.getAll,
        dsRepo.isShuffled
    ){ musicList, isShuffled ->
        musicList.toList()

    }

    val currentMusic = MutableStateFlow<Music>(dummyMusic)

}