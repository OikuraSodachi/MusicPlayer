package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository

class CustomPlayer_R(
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
):CustomPlayerNew(dsRepo) {

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