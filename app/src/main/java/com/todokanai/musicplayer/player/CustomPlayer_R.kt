package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.repository.MusicRepository
import javax.inject.Inject

class CustomPlayer_R @Inject constructor(
    musicRepo:MusicRepository,
    dsRepo:DataStoreRepository,
):CustomPlayerNew(dsRepo,musicRepo),MediaInterfaceNew {

    override fun setMusic(music: Music) {
        TODO("Not yet implemented")
    }

    override fun repeatAction() {
        TODO("Not yet implemented")
    }

    override fun prevAction() {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction() {
        TODO("Not yet implemented")
    }

    override fun nextAction() {
        TODO("Not yet implemented")
    }

    override fun shuffleAction() {
        TODO("Not yet implemented")
    }

}