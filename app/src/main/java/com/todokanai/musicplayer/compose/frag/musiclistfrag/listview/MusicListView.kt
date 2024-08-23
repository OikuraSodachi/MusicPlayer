package com.todokanai.musicplayer.compose.frag.musiclistfrag.listview

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.todokanai.musicplayer.data.room.Music

//import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun MusicListView(
    modifier : Modifier,
    itemList : List<Music>,
    onMusicClick:(Music)->Unit
){
    val listState = rememberLazyListState()

    //val items = remember{itemList}   // ...???

    /*
    LazyColumnScrollbar(
        listState = listState,
        thickness = 12.dp
    ) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(itemList.size) { index ->
                val music = itemList[index]

                MusicHolder(
                    modifier = Modifier
                        .clickable { onMusicClick(music) },
                    music = music
                )
            }
        }
    }

     */
    
    println("Recomposition: MusicListView")
}