package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_REPLAY
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.musicplayer.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.player.NewPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    customPlayer:NewPlayer
) : ViewModel(){

    private val mediaPlayer = customPlayer

    val isPlayingHolder: StateFlow<Boolean> = MutableStateFlow(false)

//    val isShuffledHolder: StateFlow<Boolean> = customPlayer.isShuffledHolder.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = false
//    )

    val isShuffledHolder: StateFlow<Boolean> = MutableStateFlow(false)

//    val isRepeatingHolder: StateFlow<Boolean> = customPlayer.isLoopingHolder.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = false
//    )

    val isRepeatingHolder: StateFlow<Boolean> = MutableStateFlow(false)
//    val currentMusicHolder: StateFlow<Music> = customPlayer.currentMusicHolderNew.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = dummyMusic
//    )

    val currentMusicHolder: StateFlow<Music> = MutableStateFlow(dummyMusic)
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