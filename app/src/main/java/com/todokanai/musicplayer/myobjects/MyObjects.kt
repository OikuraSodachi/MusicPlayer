package com.todokanai.musicplayer.myobjects

import android.app.PendingIntent
import android.content.Intent
import com.todokanai.musicplayer.components.activity.MainActivity
import com.todokanai.musicplayer.components.service.MusicService
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext

object MyObjects {

    /** AsyncImage 적용할 확장자 목록 **/
    val asyncImageExtension = arrayOf("jpg","mp4","m4a")
    val dummyMusic = Music("none","dummyMusic",null,null, 360000,"empty")

    /** customPlayer refactoring 편의를 위해 getter를 한군데로 묶음 **/
    val getPlayer
        get() = MusicService.customPlayer

    val mainOpenIntent = Intent(appContext, MainActivity::class.java)
    val mainIntent = PendingIntent.getActivity(appContext,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
    val repeatIntent = Intent(Constants.ACTION_REPLAY)
    val prevIntent = Intent(Constants.ACTION_SKIP_TO_PREVIOUS)
    val pausePlayIntent = Intent(Constants.ACTION_PAUSE_PLAY)
    val nextIntent = Intent(Constants.ACTION_SKIP_TO_NEXT)
    val shuffleIntent = Intent(Constants.ACTION_SHUFFLE)
}