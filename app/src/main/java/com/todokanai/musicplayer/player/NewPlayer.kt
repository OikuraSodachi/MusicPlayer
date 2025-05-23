package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface

class NewPlayer: MediaPlayer(), PlayerInterface {

    override fun getPrevMusic(): Music {
        TODO("Not yet implemented")
    }

    override fun getNextMusic(): Music {
        TODO("Not yet implemented")
    }

    override fun launchMusic(context: Context, music: Music) {
        TODO("Not yet implemented")
    }

    override fun applyShuffle() {
        TODO("Not yet implemented")
    }

    override fun onMusicListScan(context: Context) {
        TODO("Not yet implemented")
    }

    override fun requestUpdateNoti(
        mediaSession: MediaSessionCompat,
        startForegroundService: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun notification(context: Context) {
        TODO("Not yet implemented")
    }

    override fun startForeground(context: Context) {
        TODO("Not yet implemented")
    }

    override fun repeatAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun shuffleAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun isShuffled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun currentMusic(): Music {
        TODO("Not yet implemented")
    }
}