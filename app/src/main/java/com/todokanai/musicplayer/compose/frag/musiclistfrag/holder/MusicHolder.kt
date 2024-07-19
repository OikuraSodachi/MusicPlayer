package com.todokanai.musicplayer.compose.frag.musiclistfrag.holder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.tools.independent.durationText_td

@Composable
fun MusicHolder(
    modifier : Modifier,
    music: Music
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(music.getAlbumUri())
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier
                .aspectRatio(1f,false)
        )

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ){
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                text = music.title.toString()
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                text = music.artist.toString()
            )
        }

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
        ){
            /*
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                text = "text"
            )

             */

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                text = durationText_td(music.duration)
            )
        }
    }
    println("Recomposition: MusicHolder")
}

@Preview
@Composable
private fun MusicHolderPreview(){

    Surface() {
        MusicHolder(
            modifier = Modifier,
            music = dummyMusic
        )
    }
}