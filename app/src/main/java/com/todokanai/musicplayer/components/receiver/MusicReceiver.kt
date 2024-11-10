package com.todokanai.musicplayer.components.receiver

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.CustomPlayerNewWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicReceiver  : BroadcastReceiver() {

    @Inject
    lateinit var player2:CustomPlayer

    @Inject
    lateinit var player:CustomPlayerNewWrapper
    //private val player by lazy{getPlayer}

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_REPLAY -> {
                player.repeatAction(context)
            }

            ACTION_SKIP_TO_PREVIOUS -> {
                player.prevAction(context)
            }

            ACTION_PAUSE_PLAY -> {
                player.pausePlayAction(context)
            }

            ACTION_SKIP_TO_NEXT -> {
                player.nextAction(context)
            }

            ACTION_SHUFFLE -> {
                player.shuffleAction(context)
            }
        }
        context.sendBroadcast(Intent(ACTION_APPWIDGET_UPDATE))
    }
}