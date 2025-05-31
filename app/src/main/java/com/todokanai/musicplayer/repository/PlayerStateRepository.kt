package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.SavableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/** NewPlayer 의 상태 변화를 UI 로 전달  **/
@Singleton
class PlayerStateRepository @Inject constructor(
    private val playListRepo:PlayListRepository,
    private val dsRepo: DataStoreRepository,
    private val musicRepo: MusicRepository
) : PlayerInterface {

    private val isPlayingHolder = MutableStateFlow<Boolean>(false)

    private val isLoopingHolder : SavableStateFlow<Boolean> = dsRepo.isLoopingSavable

    private val currentMusicHolder : SavableStateFlow<Music> = musicRepo.currentMusic

    val musicStateFlow by lazy {
        combine(
            isPlayingHolder,
            isLoopingHolder,
            playListRepo.isShuffledHolder,
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

    override fun onStart(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    override fun onPause(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    override fun onStop(isPlaying: Boolean) {
        isPlayingHolder.value = isPlaying
    }

    override fun setLooping(isLooping: Boolean) {
        isLoopingHolder.value = isLooping
    }

    override fun setCurrentMusic(music: Music) {
        currentMusicHolder.value = music
    }


}

data class MusicState(
    val isPlaying:Boolean = false,
    val isLooping:Boolean = false,
    val isShuffled:Boolean = false,
    val currentMusic: Music = dummyMusic
)