package com.todokanai.musicplayer.compose.frag.testfrag

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todokanai.musicplayer.viewmodel.TestViewModel

/** dummyData **/
@Composable
fun TestFrag(
    modifier: Modifier,
    viewModel:TestViewModel = hiltViewModel()
){
    val isLooping = viewModel.isLooping.collectAsStateWithLifecycle()
    val isShuffled = viewModel.isShuffled.collectAsStateWithLifecycle()
    val isPlaying = viewModel.isPlaying.collectAsStateWithLifecycle()
   // val playList = viewModel.playList.collectAsStateWithLifecycle()
    val currentMusic = viewModel.currentMusic.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        Text("isLooping: ${isLooping.value}")

        Text("isShuffled: ${isShuffled.value}")

        Text("isPlaying: ${isPlaying.value}")

        Text("currentMusic: ${currentMusic.value?.title}")

     //   Text("playList: ${playList.value}")
    }
}