package com.todokanai.musicplayer.compose.frag

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.todokanai.musicplayer.compose.frag.musiclistfrag.MusicListFrag
import com.todokanai.musicplayer.compose.frag.playingfrag.PlayingFrag
import com.todokanai.musicplayer.compose.frag.settingsfrag.SettingsFrag
import com.todokanai.musicplayer.compose.frag.testfrag.TestFrag
import com.todokanai.musicplayer.myobjects.Constants.FRAG_MUSIC_LIST
import com.todokanai.musicplayer.myobjects.Constants.FRAG_PLAYING
import com.todokanai.musicplayer.myobjects.Constants.FRAG_SETTINGS
import com.todokanai.musicplayer.myobjects.Constants.FRAG_TEST

@Composable
fun MainFrag(
    modifier : Modifier,
    fragCode: State<String>
){
   // val fragCode = remember{ mutableSetOf<String>(FRAG_MUSIC_LIST) }

    when(fragCode.value){
        FRAG_MUSIC_LIST ->{
            MusicListFrag(
                modifier = modifier
                    .fillMaxSize()
            )
        }

        FRAG_PLAYING ->{
            PlayingFrag(
                modifier = modifier
                    .fillMaxSize()
            )
        }

        FRAG_SETTINGS ->{
            SettingsFrag(
                modifier = modifier
                    .fillMaxSize()
            )
        }

        FRAG_TEST ->{
            TestFrag(
                modifier = modifier
                    .fillMaxSize()
            )
        }
    }

    println("Recomposition: MainFrag")
}