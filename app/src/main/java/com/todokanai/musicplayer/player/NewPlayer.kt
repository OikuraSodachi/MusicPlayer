package com.todokanai.musicplayer.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.repository.PlayListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayer @Inject constructor(
    mediaSession:MediaSessionCompat,
    val playListRepo:PlayListRepository
):BasicPlayer(), PlayerInterface {

    override fun onMusicListScan(context: Context) {
        TODO("Not yet implemented")
    }

    override fun repeatAction(context: Context) {
        isLooping = !isLooping
    }

    override fun pausePlayAction(context: Context) {
        if(!isPlaying){
            start()
        }else{
            pause()
        }
    }

    override fun launchMusic(context: Context,music: Music){
        setMusic_td(context, music)
        start()
    }

    //---------------
    // 이 세개는 player 의 외부에 배치해야 할지도?
    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }
    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }
    override fun shuffleAction(context: Context) {
        TODO("Not yet implemented")
    }
}