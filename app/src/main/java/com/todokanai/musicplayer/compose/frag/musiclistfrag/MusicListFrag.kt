package com.todokanai.musicplayer.compose.frag.musiclistfrag

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.todokanai.musicplayer.viewmodel.MusicListViewModel

@Composable
fun MusicListFrag(
    modifier : Modifier,
    viewModel: MusicListViewModel = hiltViewModel()
){


    /*
    val itemList = viewModel.itemList.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        MusicListView(
            modifier = Modifier
                .weight(1f),
            itemList = itemList.value,
            onMusicClick = {}
        )
    }

     */
    println("Recomposition: MusicListFrag")

}