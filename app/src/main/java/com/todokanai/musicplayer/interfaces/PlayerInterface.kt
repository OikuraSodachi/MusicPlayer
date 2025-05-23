package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music

interface PlayerInterface {

  //  fun initAttributes(context: Context)

//    /** returns previous item of playList **/
//    fun getPrevMusic(): Music
//
//    /** returns next item of playList **/
//    fun getNextMusic(): Music

    fun launchMusic(context: Context, music: Music)

  //  fun applyShuffle()

    fun onMusicListScan(context: Context)

    fun repeatAction(context: Context)

    fun prevAction(context: Context)

    fun pausePlayAction(context: Context)

    fun nextAction(context: Context)

    fun shuffleAction(context: Context)

   // fun isShuffled():Boolean

    //fun isLooping():Boolean

    //fun currentMusic():Music
}