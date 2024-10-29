package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music

interface MediaInterfaceNew {

    fun repeatAction(isLooping:Boolean)

    fun prevAction(context: Context,currentMusic:Music,playList: List<Music>)

    fun pausePlayAction(isPlaying:Boolean)

    fun nextAction(context: Context,currentMusic: Music,playList:List<Music>)

    fun shuffleAction(isShuffled:Boolean)

    //val isLoopingHolder: Flow<Boolean>

    //val isShuffledHolder:Flow<Boolean>

   // val currentMusicHolder:Flow<Music?>

    //val isPlayingHolder:Flow<Boolean>
}