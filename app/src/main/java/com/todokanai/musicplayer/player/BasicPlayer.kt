package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** 값 적용 먼저 하고, holder 에 결과를 반영하는 구조 **/
abstract class BasicPlayer(val musicRepo:MusicRepository,val dsRepo:DataStoreRepository) : MediaPlayer() {

    private val _isLoopingHolder = MutableStateFlow<Boolean>(false)
    val isLoopingHolder = _isLoopingHolder.asStateFlow()

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder = _isPlayingHolder.asStateFlow()

    private val _currentMusicHolder = MutableStateFlow<Music>(dummyMusic)
    val currentMusicHolder = _currentMusicHolder.asStateFlow()

    override fun start() {
        super.start()
        _isPlayingHolder.value = isPlaying
    }

    override fun pause() {
        super.pause()
        _isPlayingHolder.value = isPlaying
    }

    override fun stop() {
        super.stop()
        _isPlayingHolder.value = isPlaying
    }

    override fun setLooping(p0: Boolean) {
        super.setLooping(p0)
        _isLoopingHolder.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(p0)
        }
    }

    override fun reset() {
        super.reset()
    }

    fun setMusic_td(context: Context, music: Music){
        val wasLooping = isLooping
        val isMusicValid = music.fileDir != "empty"

        if (isMusicValid) {
            reset()
            setDataSource(context, music.getUri())
            setOnCompletionListener {
                if (!isLooping) {
                    context.sendBroadcast(nextIntent)
                }
            }
            prepare()
            _currentMusicHolder.value = music
            isLooping = wasLooping
            CoroutineScope(Dispatchers.IO).launch {
                dsRepo.saveCurrentMusic(music.fileDir)
            }
        }
    }

    fun onInit(context: Context){
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        CoroutineScope(Dispatchers.IO).launch {
            setMusic_td( context, musicRepo.currentMusic.first())
            isLooping = dsRepo.isLooping()
        }
    }
}