package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.PlayListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayer @Inject constructor(mediaPlayer:MediaPlayer,mediaSession:MediaSessionCompat, val playListRepo:PlayListRepository):BasePlayer(mediaPlayer,mediaSession), PlayerInterface {

    override fun onMusicListScan(context: Context) {
        TODO("Not yet implemented")
    }

    override fun repeatAction(context: Context) {
        mediaPlayer.isLooping = !mediaPlayer.isLooping
    }

    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction(context: Context) {
        if(mediaPlayer.isPlaying){
            mediaPlayer.start()
        }else{
            mediaPlayer.pause()
        }
    }

    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun shuffleAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun isShuffled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLooping(): Boolean {
        TODO("Not yet implemented")
    }

    override fun currentMusic(): Music {
        TODO("Not yet implemented")
    }


//    private fun MediaPlayer.setMusic(context: Context, music: Music){
//        val isMusicValid = music.fileDir != "empty"
//
//        if (isMusicValid) {
//            mediaPlayer.run {
//                reset()
//                setDataSource(context, music.getUri())
//                setOnCompletionListener {
//                    if (!isLooping) {
//                        context.sendBroadcast(nextIntent)
//                    }
//                }
//                prepare()
//            }
//        }
//    }
}