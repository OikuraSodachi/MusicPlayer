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
    mediaPlayer:MediaPlayer,
    mediaSession:MediaSessionCompat,
    val playListRepo:PlayListRepository
):BasePlayer(mediaPlayer), PlayerInterface {

    private val _isLoopingHolder = MutableStateFlow<Boolean>(false)
    val isLoopingHolder = _isLoopingHolder.asStateFlow()

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder = _isPlayingHolder.asStateFlow()

    private val _isShuffledHolder = MutableStateFlow<Boolean>(false)
    val isShuffledHolder = _isShuffledHolder.asStateFlow()

    private val _currentMusicHolder = MutableStateFlow<Music>(dummyMusic)
    val currentMusicHolder = _currentMusicHolder.asStateFlow()

    override fun onMusicListScan(context: Context) {
        TODO("Not yet implemented")
    }

    override fun repeatAction(context: Context) {
        mediaPlayer.isLooping = !mediaPlayer.isLooping
       // _isLoopingHolder.value = mediaPlayer.isLooping
    }

    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction(context: Context) {
        if(mediaPlayer.isPlaying){
            mediaPlayer.start()
        }else{
            mediaPlayer.pause()
        }
    }

    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun shuffleAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun updateViewLayer(
        isPlaying: Boolean,
        isLooping: Boolean,
        isShuffled: Boolean,
        currentMusic: Music
    ) {
        TODO("Not yet implemented")
    }

    override fun launchMusic(context: Context,music: Music){
        mediaPlayer.run{
            setMusic_td(context, music)
            start()
        }
    }

    fun MediaPlayer.setMusic_td(context: Context, music: Music){
        val isMusicValid = music.fileDir != "empty"

        if (isMusicValid) {
            mediaPlayer.run {
                reset()
                setDataSource(context, music.getUri())
                setOnCompletionListener {
                    if (!isLooping) {
                        context.sendBroadcast(nextIntent)
                    }
                }
                prepare()
            }
        }
    }
}