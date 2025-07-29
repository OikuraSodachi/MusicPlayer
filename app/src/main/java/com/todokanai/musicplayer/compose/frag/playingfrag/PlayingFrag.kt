package com.todokanai.musicplayer.compose.frag.playingfrag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todokanai.musicplayer.compose.frag.playingfrag.buttons.PlayingFragButtons
import com.todokanai.musicplayer.viewmodel.PlayingViewModel

@Composable
fun PlayingFrag(
    modifier : Modifier,
    viewModel: PlayingViewModel = hiltViewModel()
){
  //  val mediaPlayer = viewModel.newMediaPlayer
    val currentMusicFlow = viewModel.currentMusicHolder.collectAsStateWithLifecycle()
    val isPlaying = viewModel.isPlayingHolder.collectAsStateWithLifecycle()
    val isRepeating =viewModel.isRepeatingHolder.collectAsStateWithLifecycle()
    val isShuffled = viewModel.isShuffledHolder.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .wrapContentHeight()
    ){
        /*
        ProgressView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .wrapContentSize(),
            currentMusic = currentMusicFlow.value,
            mediaPlayer = mediaPlayer,
            isPlaying = isPlaying.value
        )

         */

        PlayingFragButtons(
            modifier = Modifier
                .height(100.dp),
            repeat = {viewModel.repeat(it)},
            prev = {viewModel.prev(it)},
            pausePlay = {viewModel.pausePlay(it)},
            next = {viewModel.next(it)},
            shuffle ={viewModel.shuffle(it)},
            isPlaying = isPlaying.value,
            isRepeating = isRepeating.value,
            isShuffled = isShuffled.value
        )

        Spacer(modifier = Modifier
            .height(75.dp)
        )
    }

    println("Recomposition: PlayingFrag")
}