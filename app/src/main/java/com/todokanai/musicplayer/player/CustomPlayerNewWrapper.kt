package com.todokanai.musicplayer.player

import android.content.Context
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.repository.MusicRepository

/** Action 위주로 구성된 최상위  wrapper **/
class CustomPlayerNewWrapper(
    val dsRepo:DataStoreRepository,
    val musicRepo:MusicRepository
):CustomPlayerNew(dsRepo,musicRepo) {

    fun repeatAction(){
        isLooping = !isLooping
    }

    fun prevAction(context: Context){
        val prevMusic = getPrevMusic()
        launchAction(context,prevMusic)
    }

    fun pausePlayAction(){
        if(isPlaying){
            start()
        }else{
            pause()
        }
    }

    fun nextAction(context: Context){
        val nextMusic = getNextMusic()
        launchAction(context,nextMusic)
    }

    fun shuffleAction(){

    }


}