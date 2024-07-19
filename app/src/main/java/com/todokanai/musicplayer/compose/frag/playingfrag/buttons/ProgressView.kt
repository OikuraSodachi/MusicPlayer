package com.todokanai.musicplayer.compose.frag.playingfrag.buttons

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todokanai.musicplayer.compose.presets.image.ImageHolder
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.tools.independent.durationText_td
import kotlinx.coroutines.delay

@Composable
fun ProgressView(
    modifier:Modifier = Modifier,
    currentMusic:Music?,
    mediaPlayer: MediaPlayer,
    isPlaying:Boolean
) {
    var progress by remember { mutableStateOf<Int>(mediaPlayer.currentPosition) }
    val duration: Int = currentMusic?.duration ?: 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        ImageHolder(
            modifier = Modifier
                .aspectRatio(1f, false)
                .align(Alignment.CenterHorizontally)
                .padding(50.dp),
            isAsyncImage = true,
            icon = null,
            data = currentMusic?.getAlbumUri()
        )
        Row {
            Text(
                modifier = Modifier
                    .padding(20.dp),
                text = durationText_td(progress)
            )
            Slider(
                modifier = Modifier
                    .weight(1f),
                value = progress.toFloat(),
                valueRange = 0f..duration.toFloat(),
                onValueChange = {
                    mediaPlayer.seekTo(it.toInt())
                    progress = it.toInt()
                }
            )
            Text(
                modifier = Modifier
                    .padding(20.dp),
                text = durationText_td(duration)
            )
        }

        Text(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentSize(),
            text = currentMusic?.title ?: "null title"
        )
    }

    if(isPlaying) {
        LaunchedEffect(key1 = true) {
            while (true) {

                progress = mediaPlayer.currentPosition
          //      println("pos: ${mediaPlayer.currentPosition / 1000}")
                delay(1000)
            }
        }
    }
}