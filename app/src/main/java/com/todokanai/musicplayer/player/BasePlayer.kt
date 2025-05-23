package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent

abstract class BasePlayer(val mediaPlayer: MediaPlayer, val mediaSession: MediaSessionCompat) {

    abstract fun setLooping(isLooping: Boolean)
    abstract fun setShuffled(isShuffled: Boolean)
    abstract fun play()
    abstract fun pause()

    fun launchMusic(context: Context,music: Music){
        mediaPlayer.run{
            setMusic_td(context, music)
            start()
        }
    }

    abstract fun updateViewLayer(isPlaying:Boolean,isLooping:Boolean,isShuffled:Boolean,currentMusic: Music)
    private fun MediaPlayer.setMusic_td(context: Context, music: Music){
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