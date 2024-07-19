package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.myobjects.LateInitObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor() : ViewModel(){

    private val customPlayer = LateInitObjects.customPlayer

    val mediaPlayer = customPlayer.mediaPlayer

    val isPlayingHolder = customPlayer.isPlayingHolder

    val isShuffledHolder = customPlayer.isShuffledHolder

    val isRepeatingHolder = customPlayer.isLoopingHolder

    val currentMusicHolder = customPlayer.currentMusicHolder

    fun pausePlay(context:Context) = context.sendBroadcast(Intent(ACTION_PAUSE_PLAY))

    fun prev(context:Context) = context.sendBroadcast(Intent(ACTION_SKIP_TO_PREVIOUS))

    fun next(context:Context) = context.sendBroadcast(Intent(ACTION_SKIP_TO_NEXT))

    fun shuffle(context:Context) = context.sendBroadcast(Intent(ACTION_SHUFFLE))

    fun repeat(context:Context) = context.sendBroadcast(Intent(ACTION_REPLAY))
}