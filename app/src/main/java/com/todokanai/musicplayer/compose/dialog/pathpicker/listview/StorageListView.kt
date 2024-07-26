package com.todokanai.musicplayer.compose.dialog.pathpicker.listview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.todokanai.musicplayer.compose.dialog.pathpicker.holder.FileHolder
import java.io.File

@Composable
fun StorageListView(
    modifier: Modifier,
    itemList:List<File>,
    onClick: (File) -> Unit
){
    LazyColumn(
        modifier = modifier
    ){
        items(itemList.size){ index ->
            val file = itemList[index]
            FileHolder(
                modifier = Modifier,
                onClick = { onClick(file) },
                file = file
            )
            if(index<itemList.lastIndex){
                Divider()
            }
        }
    }
}