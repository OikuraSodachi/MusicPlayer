package com.todokanai.musicplayer.components.receiver

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicReceiver  : BroadcastReceiver() {

    private val player by lazy{customPlayer}

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_REPLAY -> {
                player.repeat()
            }

            ACTION_SKIP_TO_PREVIOUS -> {
                player.prev(context)
            }

            ACTION_PAUSE_PLAY -> {
                player.pausePlay()
            }

            ACTION_SKIP_TO_NEXT -> {
                player.next(context)
            }

            ACTION_SHUFFLE -> {
                player.shuffle()
            }
        }
        context.sendBroadcast(Intent(ACTION_APPWIDGET_UPDATE))
    }
}