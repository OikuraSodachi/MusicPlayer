package com.todokanai.musicplayer.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.components.service.MusicService.Companion.serviceIntent
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.repository.MusicRepository
import javax.inject.Inject

/** Action 위주로 구성된 최상위  wrapper **/
class CustomPlayerNewWrapper @Inject constructor(
    val dsRepo:DataStoreRepository,
    val musicRepo:MusicRepository,
    val icons:IconsRepository,
    val mediaSession:MediaSessionCompat
):CustomPlayerNew(dsRepo,musicRepo,icons) {

    fun repeatAction(context: Context){
        isLooping = !isLooping
        noti(context)
    }

    fun prevAction(context: Context){
        val prevMusic = getPrevMusic()
        launchMusic(context,prevMusic)
        noti(context)
    }

    fun pausePlayAction(context: Context){
        if(isPlaying){
            start()
        }else{
            pause()
        }
        noti(context)
    }

    fun nextAction(context: Context){
        val nextMusic = getNextMusic()
        launchMusic(context,nextMusic)
        noti(context)
    }

    fun shuffleAction(context: Context){
        applyShuffle()
        noti(context)
    }

    private fun noti(context: Context){
        requestUpdateNoti(mediaSession,{context.startForegroundService(serviceIntent)})
    }


}