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

    override fun prevAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun pausePlayAction(context: Context) {
        if(!isPlaying){
            start()
        }else{
            pause()
        }
    }

    override fun nextAction(context: Context) {
        TODO("Not yet implemented")
    }

    override fun shuffleAction(context: Context) {

    }

//    override fun updateViewLayer(
//        isPlaying: Boolean,
//        isLooping: Boolean,
//        isShuffled: Boolean,
//        currentMusic: Music
//    ) {
//        TODO("Not yet implemented")
//    }

    override fun launchMusic(context: Context,music: Music){
        setMusic_td(context, music)
        start()
    }

//    private fun setMusic_td(context: Context, music: Music){
//        val isMusicValid = music.fileDir != "empty"
//
//        if (isMusicValid) {
//            reset()
//            setDataSource(context, music.getUri())
//            setOnCompletionListener {
//                if (!isLooping) {
//                    context.sendBroadcast(nextIntent)
//                }
//            }
//            prepare()
//            _currentMusicHolder.value = music
//        }
//    }
}