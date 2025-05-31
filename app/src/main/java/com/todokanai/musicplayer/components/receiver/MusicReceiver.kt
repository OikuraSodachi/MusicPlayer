package com.todokanai.musicplayer.components.receiver

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicReceiver  : BroadcastReceiver() {

    @Inject
    lateinit var player:PlayerInterface

    override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_REPLAY -> {
                    player.repeatAction()
                }

                ACTION_SKIP_TO_PREVIOUS -> {
                    player.toPrevMusic(context)
                }

                ACTION_PAUSE_PLAY -> {
                    player.pausePlayAction()
                }

                ACTION_SKIP_TO_NEXT -> {
                    player.toNextMusic(context)
                }

                ACTION_SHUFFLE -> {
                    player.toggleShuffle()
                }
            }
            if (isWidgetActive()) {
                context.sendBroadcast(Intent(ACTION_APPWIDGET_UPDATE))
            }
    }

    /** Todo: Widget 활성화 여부 판별하기 **/
    fun isWidgetActive():Boolean{
        return true
    }
}