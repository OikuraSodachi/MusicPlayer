package com.todokanai.musicplayer.player

import android.content.Context
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import javax.inject.Inject

class CustomPlayer_R @Inject constructor(
    musicRepo:MusicRepository,
    dsRepo:DataStoreRepository,
):CustomPlayerNew(dsRepo,musicRepo),MediaInterfaceNew {

    override val isLoopingHolder = _isLoopingHolder
    override val isShuffledHolder = _isShuffledHolder
    override val currentMusicHolder = _currentMusicHolder
    override val isPlayingHolder = _isPlayingHolder

    override fun repeatAction(isLooping:Boolean) {
        saveLoop(!isLooping)
    }

    override fun prevAction(context: Context,currentMusic: Music) {
        try{
            val playList = getPlayList()
            launchMusic(
                context,
                getCircularPrev_td(getPlayList(), playList.indexOf(currentMusic))
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun pausePlayAction(isPlaying:Boolean) {
        TODO("Not yet implemented")
    }

    override fun nextAction(context: Context,currentMusic: Music) {
        try{
            val playList = getPlayList()
            launchMusic(
                context,
                getCircularNext_td(getPlayList(), playList.indexOf(currentMusic))
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun shuffleAction(isShuffled:Boolean) {
        saveShuffle(!isShuffled)
    }
}