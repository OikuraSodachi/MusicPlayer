package com.todokanai.musicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.player.NewPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    val player:NewPlayer
) : ViewModel(){

//    /** list of music ( not playList ) **/
//    private val sortedList: Flow<List<Music>> = player.playListFlow.map{
//        it.sortedBy { music ->
//            music.title
//        }
//    }
    /** list of music ( not playList ) **/
    private val sortedList: Flow<List<Music>> = emptyFlow()


    val itemList: Flow<List<MusicHolderItem>> = sortedList.map{
        it.map{ music ->
            music.toMusicHolderItem()
        }
    }

    fun onMusicClick(context: Context, music: MusicHolderItem) {
        viewModelScope.launch {
            player.launchMusic(context,music.music)
        }
    }
    /*
    fun floatingButton(activity:Activity){
        viewModelScope.launch {
            exit_td(activity,serviceIntent)
        }
    }
     */
}