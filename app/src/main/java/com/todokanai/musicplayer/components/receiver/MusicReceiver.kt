package com.todokanai.musicplayer.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.myobjects.LateInitObjects.customPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicReceiver  : BroadcastReceiver() {

    private val player by lazy{customPlayer}

    override fun onReceive(context: Context, intent: Intent) {
        val playList = player.playListHolder.value
        val currentMusic = player.currentMusicHolder.value
        val isShuffled = player.isShuffledHolder.value

        when (intent.action) {
            ACTION_REPLAY -> {
                player.repeat()
            }

            ACTION_SKIP_TO_PREVIOUS -> {
                currentMusic?.let {
                    player.prev(context, it, playList)
                }
            }

            ACTION_PAUSE_PLAY -> {
                currentMusic?.let {
                    player.pausePlay()
                }
            }

            ACTION_SKIP_TO_NEXT -> {
                currentMusic?.let {
                    player.next(context, it, playList)
                }
            }

            ACTION_SHUFFLE -> {
                player.shuffle(isShuffled)
            }
        }
    }
}