package com.todokanai.musicplayer.compose.dialog.pathpicker.listview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.dialog.pathpicker.holder.FileHolder
import com.todokanai.musicplayer.data.dataclass.FileHolderItem
import java.io.File

/** Recomposition 최적화 Confirm 안된 상태 **/
@Composable
fun FileListView(
    modifier: Modifier,
    itemList:List<FileHolderItem>,
    onClick: (File) -> Unit,
    toParent: () -> Unit,
){

    LazyColumn(
        modifier = modifier
          //  .fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clickable { toParent() },
                text = stringResource(id = R.string.to_parent_directory)
            )
        }
        items(itemList.size) { index ->
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
    println("Recomposition: FileListView")
}