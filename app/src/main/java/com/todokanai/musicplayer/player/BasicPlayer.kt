package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerStateInterface
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

/** 값 적용 먼저 하고, holder 에 결과를 반영하는 구조
 * Todo: 값 저장은 여기서는 다루지 말 것.
 *  Todo: start, pause, reset 등 메소드에 MediaPlaybackState setter 를 포함시켜야 할지도? **/
abstract class BasicPlayer(
    val dsRepo: DataStoreRepository,
    val musicRepo: MusicRepository
) : MediaPlayer(),PlayerStateInterface {

    private val isShuffledHolder = dsRepo.isShuffledSavable

    private val seedHolder = dsRepo.seedSavable
    /** Todo: seed value 저장 기능 not implemented **/

    private val isPlayingHolder = MutableStateFlow<Boolean>(false)

    private val isLoopingHolder = dsRepo.isLoopingSavable

    private val currentMusicHolder = musicRepo.currentMusic

    override fun start() {
        super.start()
        isPlayingHolder.value = isPlaying
    }

    override fun pause() {
        super.pause()
        isPlayingHolder.value = isPlaying
    }

    override fun stop() {
        super.stop()
        isPlayingHolder.value = isPlaying
    }

    override fun setLooping(p0: Boolean) {
        super.setLooping(p0)
        isLoopingHolder.value = isLooping
    }

    open fun onSetCurrentMusic(music: Music){
        currentMusicHolder.value = music
    }

    open fun onToggleShuffle(){
        val wasShuffled = isShuffledHolder.value
        isShuffledHolder.value = !wasShuffled
    }


    //-------------

    override val musicStateFlow by lazy {
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

    override val playList = combine(
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
        println("list: ${playList.map{it.title}}")
        val prevMusic = getCircularPrev_td(playList, currentMusic)
        val nextMusic = getCircularNext_td(playList,currentMusic)
        Triple(prevMusic,currentMusic,nextMusic)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = Triple(dummyMusic, dummyMusic, dummyMusic)
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