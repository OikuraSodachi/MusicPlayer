package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

abstract class PlayListManager_abstract(
    initialMusic: Music,
    initialPlayList:List<Music>,
    initialShuffle:Boolean,
    initialSeed:Double,
    val dsRepo:DataStoreRepository,
    val musicRepo: MusicRepository
) {
    val shuffledFlow = MutableStateFlow<Boolean>(initialShuffle)
    private val seedFlow = MutableStateFlow<Double>(initialSeed)
    val currentMusicFlow = MutableStateFlow<Music>(initialMusic)
    private val playListFlow = MutableStateFlow<List<Music>>(initialPlayList)


    fun updateShuffle(isShuffled:Boolean){
        shuffledFlow.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    fun updateSeed(seed:Double){
        seedFlow.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveRandomSeed(seed)
        }
    }

    fun updateCurrentMusic(music: Music){
        currentMusicFlow.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    suspend fun updatePlayList(){
        playListFlow.value = modifiedPlayList(musicRepo.getAllNonFlow(),isShuffled(),seedFlow.value)
    }

    private fun modifiedPlayList(musics:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musics.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musics.sortedBy { it.title }
        }
    }

    fun currentMusic():Music{
        return currentMusicFlow.value
    }

    fun isShuffled():Boolean{
        return shuffledFlow.value
    }

    fun playList():List<Music>{
        return playListFlow.value
    }

    suspend fun initValues(){
        shuffledFlow.value = dsRepo.isShuffled()
        seedFlow.value = dsRepo.getSeed()
        currentMusicFlow.value = musicRepo.currentMusicNonFlow()
        updatePlayList()
    }

    abstract fun getNextMusic():Music

    abstract fun getPrevMusic():Music

}