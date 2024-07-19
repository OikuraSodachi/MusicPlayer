package com.todokanai.musicplayer.compose.dialog.pathpicker.listview

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todokanai.musicplayer.compose.dialog.pathpicker.holder.DirectoryHolder
import java.io.File

/** LazyRow recomposition 최적화 아직 안됨
 *
 * item 하나 바뀌면 List 전체가 recompose 되고 있음
 * **/
@Composable
fun DirectoryListView(
    modifier:Modifier = Modifier,
    dirTree: List<File>,
    updateCurrentPath:(File)->Unit
){
    LazyRow(
        modifier = modifier
            .height(40.dp)
    ){
        items(dirTree.size){ index ->
            val item = dirTree[index]

            DirectoryHolder(
                modifier = Modifier
                    .fillMaxHeight(),
                fileName = {item.name},
                onClick = {updateCurrentPath(item)}
            )
        }
    }
}