package com.todokanai.musicplayer.compose.dialog.pathpicker.listview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.todokanai.musicplayer.compose.dialog.pathpicker.holder.FileHolder
import com.todokanai.musicplayer.data.dataclass.FileHolderItem
import java.io.File

@Composable
fun StorageListView(
    modifier: Modifier,
    itemList:List<FileHolderItem>,
    onClick: (File) -> Unit
){
    LazyColumn(
        modifier = modifier
    ){
        items(itemList.size){ index ->
            val fileHolderItem = itemList[index]
            FileHolder(
                modifier = Modifier
                    .clickable (onClick = { onClick(fileHolderItem.file) }),
                fileHolderItem = fileHolderItem
            )
            if(index<itemList.lastIndex){
                Divider()
            }
        }
    }
}