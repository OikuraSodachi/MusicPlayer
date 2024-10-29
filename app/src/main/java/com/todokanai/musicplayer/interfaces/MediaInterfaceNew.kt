package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

interface MediaInterfaceNew {

    fun repeatAction(isLooping:Boolean)

    fun prevAction(context: Context,currentMusic:Music)

    fun pausePlayAction(isPlaying:Boolean)

    fun nextAction(context: Context,currentMusic: Music)

    fun shuffleAction(isShuffled:Boolean)

    val isLoopingHolder: StateFlow<Boolean>

    val isShuffledHolder:StateFlow<Boolean>

    val currentMusicHolder:StateFlow<Music?>

    val isPlayingHolder:StateFlow<Boolean>
}