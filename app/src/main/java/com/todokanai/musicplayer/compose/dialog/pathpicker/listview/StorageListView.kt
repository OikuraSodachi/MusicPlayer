package com.todokanai.musicplayer.compose.dialog.pathpicker.listview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.todokanai.musicplayer.compose.dialog.pathpicker.holder.FileHolder
import java.io.File

@Composable
fun StorageListView(
    itemList:List<File>,
    onClick: (File) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ){
        items(itemList.size){ index ->
            val file = itemList[index]
            FileHolder(
                file = file,
                onClick = { onClick(file) }
            )
            if(index<itemList.lastIndex){
                Divider()
            }
        }
    }
}