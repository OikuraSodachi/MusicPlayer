package com.todokanai.musicplayer.compose.presets.image

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageHolder(
    isAsyncImage:Boolean,
    icon: Painter?,
    data: Uri?,
    modifier: Modifier = Modifier
){
    if(isAsyncImage){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier,
            placeholder = icon
        )
    } else {
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = modifier
            )
        }
    }
}