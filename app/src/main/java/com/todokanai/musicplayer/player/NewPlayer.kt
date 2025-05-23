package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.PlayListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayer @Inject constructor(
    mediaSession:MediaSessionCompat,
    val playListRepo:PlayListRepository
):MediaPlayer(), PlayerInterface {

    private val _isLoopingHolder = MutableStateFlow<Boolean>(false)
    val isLoopingHolder = _isLoopingHolder.asStateFlow()

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder = _isPlayingHolder.asStateFlow()

    private val _currentMusicHolder = MutableStateFlow<Music>(dummyMusic)
    val currentMusicHolder = _currentMusicHolder.asStateFlow()

    override fun start(){
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

    override fun onMusicListScan(context: Context) {
        TODO("Not yet implemented")
    }

    override fun repeatAction(context: Context) {
        isLooping = !isLooping
        _isLoopingHolder.value = isLooping
    }

    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction(context: Context) {
        if(!isPlaying){
            start()
        }else{
            pause()
        }
    }

    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun shuffleAction(context: Context) {

    }

//    override fun updateViewLayer(
//        isPlaying: Boolean,
//        isLooping: Boolean,
//        isShuffled: Boolean,
//        currentMusic: Music
//    ) {
//        TODO("Not yet implemented")
//    }

    override fun launchMusic(context: Context,music: Music){
        setMusic_td(context, music)
        start()
    }

    private fun setMusic_td(context: Context, music: Music){
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