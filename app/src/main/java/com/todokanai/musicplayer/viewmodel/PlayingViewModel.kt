package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.repository.PlayerStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    customPlayer:NewPlayer,
    val stateRepo:PlayerStateRepository
) : ViewModel(){


    //-----------
    private val mediaPlayer = customPlayer

    val isPlayingHolder: StateFlow<Boolean> =customPlayer.isPlayingHolder

    val isShuffledHolder: StateFlow<Boolean> = stateRepo.isShuffledHolder.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isRepeatingHolder: StateFlow<Boolean> = customPlayer.isLoopingHolder

    val currentMusicHolder: StateFlow<Music> = customPlayer.currentMusicHolder
    //----------------

    fun duration() = mediaPlayer.duration

    fun currentPosition() = mediaPlayer.currentPosition

    fun seekTo(progress:Int) = mediaPlayer.seekTo(progress)

    fun pausePlay(context:Context) = context.sendBroadcast(Intent(ACTION_PAUSE_PLAY))

    fun prev(context:Context) = context.sendBroadcast(Intent(ACTION_SKIP_TO_PREVIOUS))

    fun next(context:Context) = context.sendBroadcast(Intent(ACTION_SKIP_TO_NEXT))

    fun shuffle(context:Context) = context.sendBroadcast(Intent(ACTION_SHUFFLE))

    fun repeat(context:Context) = context.sendBroadcast(Intent(ACTION_REPLAY))
}