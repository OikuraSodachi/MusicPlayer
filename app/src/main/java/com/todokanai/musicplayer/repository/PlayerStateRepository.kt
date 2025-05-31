package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/** NewPlayer 의 상태 변화를 UI 로 전달  **/
@Singleton
class PlayerStateRepository @Inject constructor(
    dsRepo: DataStoreRepository,
    musicRepo: MusicRepository
)  {

    private val isShuffledHolder = dsRepo.isShuffledSavable

    private val seedHolder = dsRepo.seedSavable
    /** Todo: seed value 저장 기능 not implemented **/

    private val isPlayingHolder = MutableStateFlow<Boolean>(false)

    private val isLoopingHolder = dsRepo.isLoopingSavable

    private val currentMusicHolder = musicRepo.currentMusic

    val musicStateFlow by lazy {
        combine(
            isPlayingHolder,
            isLoopingHolder,
            isShuffledHolder,
            currentMusicHolder
        ) { isPlaying, isLooping, isShuffled, currentMusic ->
            MusicState(
                isPlaying = isPlaying,
                isLooping = isLooping,
                isShuffled = isShuffled,
                currentMusic = currentMusic
            )
        }
    }

    fun onStart(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    fun onPause(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    fun onStop(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    fun setLooping(isLooping: Boolean) {
        isLoopingHolder.value = isLooping
    }

    fun setCurrentMusic(music: Music) {
        currentMusicHolder.value = music
    }

    fun toggleShuffle(){
        val wasShuffled = isShuffledHolder.value
        isShuffledHolder.value = !wasShuffled
    }

    //-----------
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

    fun prevMusic() = musicCache.value.first
    fun nextMusic() = musicCache.value.third
}

data class MusicState(
    val isPlaying:Boolean = false,
    val isLooping:Boolean = false,
    val isShuffled:Boolean = false,
    val currentMusic: Music = dummyMusic
)