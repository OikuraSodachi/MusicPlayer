package com.todokanai.musicplayer.compose.frag.playingfrag.buttons

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.todokanai.musicplayer.R

@Composable
fun PlayingFragButtons(
    modifier : Modifier = Modifier,
    repeat:(Context)->Unit,
    prev:(Context)->Unit,
    pausePlay:(Context)->Unit,
    next:(Context)->Unit,
    shuffle:(Context)->Unit,
    isRepeating:Boolean,
    isPlaying:Boolean,
    isShuffled:Boolean
){
    val context = LocalContext.current
    Row (
        modifier = modifier
            .wrapContentSize()
            .fillMaxWidth()
    ){
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { repeat(context) }
        ) {
            Image(
                painter = painterResource(
                    if(isRepeating){
                        R.drawable.baseline_repeat_one_24
                    } else {
                        R.drawable.baseline_repeat_24
                    }
                ),
                contentDescription = null
            )
        }

        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { prev(context) }
        ) {
            Image(
                painter = painterResource( R.drawable.baseline_skip_previous_24),
                contentDescription = null
            )
        }
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { pausePlay(context) }
        ) {
            Image(
                painter = painterResource(
                    if(isPlaying){
                        R.drawable.baseline_pause_24
                    } else {
                        R.drawable.baseline_play_arrow_24
                    }
                ),
                contentDescription = null
            )
        }
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { next(context) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_skip_next_24),
                contentDescription = null
            )
        }

        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { shuffle(context)}
        ){
            Image(
                painter = painterResource(
                    if(isShuffled){
                        R.drawable.baseline_shuffle_24
                    }else{
                        R.drawable.baseline_arrow_right_alt_24
                    }
                ),
                contentDescription = null
            )
        }
    }
}