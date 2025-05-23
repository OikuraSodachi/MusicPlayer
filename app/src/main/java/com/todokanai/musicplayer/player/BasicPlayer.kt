package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BasicPlayer : MediaPlayer() {

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
    }

    override fun release() {
        super.release()
    }

    override fun reset() {
        super.reset()
    }

    fun setMusic_td(context: Context, music: Music){
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
        }
    }
}