package com.todokanai.musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel(){

    private val player = customPlayer

    val isLooping = player.isLoopingHolder

    val isShuffled = player.isShuffledHolder

    val isPlaying = player.isPlayingHolder

    val currentMusic = player.currentMusicHolder
}