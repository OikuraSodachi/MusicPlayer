package com.todokanai.musicplayer.player

import android.content.Context
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td

abstract class CustomPlayerNew(
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository
):MediaInterfaceNew(dsRepo,musicRepo){

    override fun start(){
        super.start()
    }

    override fun initAttributes() {
        TODO("Not yet implemented")
    }

    override fun playList(): List<Music> {
        TODO("Not yet implemented")
    }

    fun getPrevMusic():Music{
        val list = playList()
        val current = currentMusic()
        return getCircularPrev_td(list,current)
    }

    fun getNextMusic():Music{
        val list = playList()
        val current = currentMusic()
        return getCircularNext_td(list,current)
    }


    /** == launch Music
     *
     *  Done
     * **/
    fun launchAction(context: Context, music:Music){
        setMusic(context,music)
        start()
    }
}