package com.todokanai.musicplayer.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayer @Inject constructor(
    val mediaSession:MediaSessionCompat,
    musicRepo:MusicRepository,
    val iconsRepo: IconsRepository,
    dsRepo: DataStoreRepository
):BasicPlayer(musicRepo,dsRepo), PlayerInterface {

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

    //----------------------


    suspend fun requestUpdateNoti(mediaSession: MediaSessionCompat, startForegroundService:()->Unit){
        mediaSession.setMediaPlaybackState_td(iconsRepo.loopingImage(isLooping), isPlaying, iconsRepo.shuffledImage(dsRepo.isShuffled()))
        startForegroundService()
    }

    /**
     *  MediaSessionCompat의 PlaybackState Setter
     *
     *  playback position 관련해서는 미검증 상태
     */
    fun MediaSessionCompat.setMediaPlaybackState_td(
        repeatIcon:Int,
        isPlaying:Boolean,
        shuffleIcon:Int
    ){
        fun state() = if(isPlaying){
            PlaybackStateCompat.STATE_PLAYING
        }else{
            PlaybackStateCompat.STATE_PAUSED
        }
        val state = state()
        val playbackState = PlaybackStateCompat.Builder()
            .apply {
                val actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                } else {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                }
                addCustomAction(Constants.ACTION_REPLAY,"Repeat", repeatIcon)
                addCustomAction(Constants.ACTION_SHUFFLE,"Shuffle",shuffleIcon)
                setActions(actions)
            }
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }
}