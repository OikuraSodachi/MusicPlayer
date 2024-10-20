package com.todokanai.musicplayer.myobjects

import android.app.PendingIntent
import android.content.Intent
import com.todokanai.musicplayer.components.activity.MainActivity
import com.todokanai.musicplayer.components.service.MusicService
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.player.CustomPlayer

object MyObjects {

    /** AsyncImage 적용할 확장자 목록 **/
    val asyncImageExtension = arrayOf("jpg","mp4","m4a")
    val dummyMusic = Music("none","dummyMusic",null,null, 360000,"empty")

  //  private val customPlayer = CustomPlayer()
    /** customPlayer refactoring 편의를 위해 getter를 한군데로 묶음 **/
    val getPlayer : CustomPlayer
        get() = MusicService.customPlayer



    val mainOpenIntent = Intent(appContext, MainActivity::class.java)
    val mainIntent = PendingIntent.getActivity(appContext,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
    val repeatIntent = PendingIntent.getBroadcast(appContext, 0, Intent(Constants.ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)
    val prevIntent = PendingIntent.getBroadcast(appContext, 0, Intent(Constants.ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)
    val pausePlayIntent = PendingIntent.getBroadcast(appContext, 0, Intent(Constants.ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)
    val nextIntent = PendingIntent.getBroadcast(appContext, 0, Intent(Constants.ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)
    val shuffleIntent = PendingIntent.getBroadcast(appContext, 0, Intent(Constants.ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)

}