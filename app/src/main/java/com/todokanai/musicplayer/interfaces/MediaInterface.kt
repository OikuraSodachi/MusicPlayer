package com.todokanai.musicplayer.interfaces

import android.content.Context
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.flow.StateFlow

/** Dummy Data **/
interface MediaInterface {

    val isPlayingHolder: StateFlow<Boolean>
    val seedHolder:StateFlow<Double>
    val currentMusicHolder:StateFlow<Music>
    val isLoopingHolder:StateFlow<Boolean>
    val isShuffledHolder:StateFlow<Boolean>
    val playListHolder:StateFlow<List<Music>>

    fun launchMusic(context: Context,music: Music)

    fun pausePlayAction()

    fun prevAction(context: Context)

    fun nextAction(context: Context)

    fun repeatAction()

    fun shuffleAction()
}