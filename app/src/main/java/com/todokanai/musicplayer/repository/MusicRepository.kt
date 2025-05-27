package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.data.room.MusicDao
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.SavableStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(private val musicDao:MusicDao,dsRepo: DataStoreRepository){

    val getAll = musicDao.getAll()

    val currentMusic = SavableStateFlow<Music>(
        initialValue = dummyMusic,
        saveValue = { CoroutineScope(Dispatchers.IO).launch {   dsRepo.saveCurrentMusic(it.fileDir) }}
    ).apply {
        CoroutineScope(Dispatchers.IO).launch {
            value = musicDao.getAllNonFlow().find { it.fileDir == dsRepo.currentMusic() } ?: dummyMusic
        }
    }

//    val currentMusicOriginal = combine(
//        getAll,
//        dsRepo.currentMusic
//    ){ musicList, absolutePath ->
//        musicList.find { it.fileDir == absolutePath } ?: dummyMusic
//    }
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