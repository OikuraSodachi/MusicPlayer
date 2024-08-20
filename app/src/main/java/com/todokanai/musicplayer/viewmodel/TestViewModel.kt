package com.todokanai.musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import com.todokanai.musicplayer.data.room.Music
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel(){

    lateinit var isLooping : StateFlow<Boolean> // = player.isLoopingHolder

    lateinit var isShuffled : StateFlow<Boolean> //= player.isShuffledHolder

    lateinit var isPlaying : StateFlow<Boolean> //= player.isPlayingHolder

    lateinit var currentMusic : StateFlow<Music?> //player.currentMusicHolder
}