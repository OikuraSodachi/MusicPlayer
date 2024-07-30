package com.todokanai.musicplayer.compose.frag.settingsfrag.holder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.todokanai.musicplayer.compose.presets.image.ImageHolder

/** 스캔할 경로 Holder **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScanPathHolder(
    modifier:Modifier,
    deleteIcon: Int,
    deleteItem:(String)->Unit,
    absolutePath:String
){
    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ){
        Text(
            modifier = Modifier
                .basicMarquee()
                .align(Alignment.CenterVertically)
                .padding(10.dp)
                .weight(1f),
            //text = path.absolutePath
            text = absolutePath
        )

        Button(
            onClick = {
                deleteItem(absolutePath)

            }
        ) {
            ImageHolder(
                modifier = Modifier
                    .aspectRatio(1f,false),
                isAsyncImage = false,
                icon = painterResource(deleteIcon),
                data = null
            )
        }
    }

  //  Timber.d(path.absolutePath)
}