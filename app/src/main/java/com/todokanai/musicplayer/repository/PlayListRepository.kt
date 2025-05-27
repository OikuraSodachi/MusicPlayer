package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayListRepository @Inject constructor(private val dsRepo:DataStoreRepository, musicRepo:MusicRepository) {

    //----------------
    // 상태 값 실시간 동기화를 위한 holder
    // StateHolder 값 적용 -> db 에 저장 순으로 진행 ---->>>>  Todo: SavableStateFlow 다시 만들기

    private val _isShuffledHolder = dsRepo.isShuffledSavable
    val isShuffledHolder = _isShuffledHolder.asStateFlow()

    private val _seedHolder = dsRepo.seedSavable
    /** Todo: seed value 저장 기능 not implemented **/
    val seedHolder = _seedHolder.asStateFlow()

    // Todo : prevMusicHolder, nextMusicHolder 추가?

    //-------------
    val playList = combine(
        musicRepo.getAll,
        isShuffledHolder,
        seedHolder
    ){ musicArray, isShuffled, seed ->
        val musicList = musicArray.sortedBy{it.title}
        if(isShuffled){
            musicList.shuffled(Random(seed))
        }else{
            musicList.sortedBy{it.title}
        }
    }

    /** flow of prev, current, next music **/
    private val musicCache = combine(
        playList,
        musicRepo.currentMusic
    ){ playList, currentMusic ->
        val prevMusic =
            try {
                getCircularPrev_td(playList, currentMusic)
            }catch (e:Exception){
                dummyMusic
            }
        val nextMusic =
            try{
                getCircularNext_td(playList,currentMusic)
            }catch (e:Exception){
                dummyMusic
            }

        Triple(prevMusic,currentMusic,nextMusic)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = Triple(dummyMusic,dummyMusic,dummyMusic)
    )

    fun toggleShuffle() {
        val wasShuffled = isShuffledHolder.value
        _isShuffledHolder.value = !wasShuffled
    }

    fun prevMusic() = musicCache.value.first
    fun nextMusic() = musicCache.value.third

}