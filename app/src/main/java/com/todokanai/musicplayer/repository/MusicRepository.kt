package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.data.room.MusicDao
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(private val musicDao:MusicDao,dsRepo: DataStoreRepository){

    val getAll = musicDao.getAll()

    val currentMusic = combine(
        getAll,
        dsRepo.currentMusic
    ){ musicList, absolutePath ->
        musicList.find { it.fileDir == absolutePath } ?: dummyMusic
    }
    suspend fun getAllNonFlow() = musicDao.getAllNonFlow()

    suspend fun insert(music: Music) = musicDao.insert(music)

    suspend fun deleteAll() = musicDao.deleteAll()

    suspend fun delete(music: Music) = musicDao.delete(music)

    suspend fun updateMusicList(newList:Array<Music>){
        musicDao.deleteAll()
        newList.forEach {  music ->
            musicDao.insert(music)
        }
    }

}