package com.todokanai.musicplayer.compose.dialog.pathpicker.holder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun DirectoryHolder(
    modifier: Modifier,
    fileName:()->String,
    onClick:()->Unit,
){
    Row(
        modifier = modifier
            .wrapContentWidth()
            .clickable {
                onClick()
            }
    ){
        Text(text = "/${fileName()}",
            Modifier
                .padding(horizontal = 4.dp)
                .align(Alignment.CenterVertically),
            fontSize = 14.sp
        )
    }
    println("Recomposition: DirectoryHolder")
}