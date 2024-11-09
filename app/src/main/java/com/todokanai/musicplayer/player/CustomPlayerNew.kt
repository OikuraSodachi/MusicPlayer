package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.repository.MusicRepository

class CustomPlayerNew(
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



}