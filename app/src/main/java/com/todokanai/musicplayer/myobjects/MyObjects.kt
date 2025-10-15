package com.todokanai.musicplayer.myobjects

import android.content.Intent
import com.todokanai.musicplayer.data.room.Music

object MyObjects {

    /** AsyncImage 적용할 확장자 목록 **/
    val asyncImageExtension = arrayOf("jpg","mp4","m4a")
    val dummyMusic = Music("dummyMusic","dummyMusic",null,null, 360000,"empty")

//    val mainOpenIntent = Intent(appContext, MainActivity::class.java)
//    val mainIntent = PendingIntent.getActivity(appContext,0, Intent( Intent(appContext, MainActivity::class.java)), PendingIntent.FLAG_IMMUTABLE)
   // fun serviceIntent(context: Context) = Intent(context, MusicService::class.java)
    //fun mainIntent(context: Context) = PendingIntent.getActivity(context,0, Intent( mainOpenIntent(context)), PendingIntent.FLAG_IMMUTABLE)
    val repeatIntent = Intent(Constants.ACTION_REPLAY)
    val prevIntent = Intent(Constants.ACTION_SKIP_TO_PREVIOUS)
    val pausePlayIntent = Intent(Constants.ACTION_PAUSE_PLAY)
    val nextIntent = Intent(Constants.ACTION_SKIP_TO_NEXT)
    val shuffleIntent = Intent(Constants.ACTION_SHUFFLE)
}