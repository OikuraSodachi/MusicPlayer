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

    fun isLooping():Boolean{
        return isLoopingHolder.value
    }

    fun isShuffled():Boolean{
        return isShuffledHolder.value
    }

    fun seed():Double{
        return seedHolder.value
    }

    fun playList():List<Music>{
        return playListHolder.value
    }

    fun currentMusic():Music{
        return currentMusicHolder.value
    }

    fun launchMusic(context: Context,music: Music)

    fun pausePlayAction()

    fun prevAction(context: Context)

    fun nextAction(context: Context)

    fun repeatAction()

    fun shuffleAction()
}