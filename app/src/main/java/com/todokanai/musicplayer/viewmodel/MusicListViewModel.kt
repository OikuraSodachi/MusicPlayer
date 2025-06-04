package com.todokanai.musicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.interfaces.PlayerStateInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val playerInterface:PlayerInterface,
    playerStateInterface: PlayerStateInterface
) : ViewModel(){

//    /** list of music ( not playList ) **/
//    private val sortedList: Flow<List<Music>> = player.playListFlow.map{
//        it.sortedBy { music ->
//            music.title
//        }
//    }
    /** list of music ( not playList ) **/
    private val sortedList: Flow<List<Music>> = playerStateInterface.playList.map {
        it.sortedBy { music ->
            music.title
        }
    }

    val itemList: Flow<List<MusicHolderItem>> = sortedList.map{
        it.map{ music ->
            music.toMusicHolderItem()
        }
    }

    fun onMusicClick(context: Context, music: MusicHolderItem) {
        viewModelScope.launch {
            playerInterface.launchMusic(context,music.music)
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