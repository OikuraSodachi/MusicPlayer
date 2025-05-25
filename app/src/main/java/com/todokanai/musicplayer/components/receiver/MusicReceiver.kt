package com.todokanai.musicplayer.components.receiver

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.repository.PlayListRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicReceiver  : BroadcastReceiver() {

    @Inject
    lateinit var player:NewPlayer

    @Inject
    lateinit var playListRepo:PlayListRepository

    override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_REPLAY -> {
                    player.repeatAction(context)
                }

                ACTION_SKIP_TO_PREVIOUS -> {
                    // player.prevAction(context)
                    player.launchMusic(context, getPrev())
                }

                ACTION_PAUSE_PLAY -> {
                    player.pausePlayAction(context)
                }

                ACTION_SKIP_TO_NEXT -> {
                    //player.nextAction(context)
                    player.launchMusic(context, getNext())
                }

                ACTION_SHUFFLE -> {
                    //player.shuffleAction(context)
                    playListRepo.toggleShuffle()
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

    fun getPrev(): Music = playListRepo.prevMusic()
    fun getNext(): Music = playListRepo.nextMusic()
}