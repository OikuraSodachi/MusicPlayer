package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.todokanai.musicplayer.components.service.MusicService.Companion.serviceIntent
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class CustomPlayerNew(
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository,
    val iconsRepo:IconsRepository
):MediaInterfaceNew(dsRepo,musicRepo){


    fun initAttributes(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            this.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                onInitAttributes(context)
                notification(context)
            }
        }
    }

    /** returns previous item of playList **/
    fun getPrevMusic():Music{
        val list = sortedPlayList()
        val current = currentMusic()
        return getCircularPrev_td(list,current)
    }

    /** returns next item of playList **/
    fun getNextMusic():Music{
        val list = sortedPlayList()
        val current = currentMusic()
        return getCircularNext_td(list,current)
    }

    /** == launch Music
     *
     *  Done
     * **/
    fun launchMusic(context: Context, music:Music){
        setMusic(context,music,isLooping)
        start()
        notification(context)
    }

    fun applyShuffle(){
        val shuffled = isShuffled()
        setShuffle(!isShuffled())
    }

    fun onMusicListScan(context: Context){
        initAttributes(context)
    }

    fun requestUpdateNoti(mediaSession: MediaSessionCompat, startForegroundService:()->Unit){
        mediaSession.setMediaPlaybackState_td(iconsRepo.loopingImage(isLooping), isPlaying, iconsRepo.shuffledImage(isShuffled()))
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

    abstract fun notification(context: Context)

    fun startForeground(context: Context){
        context.startForegroundService(serviceIntent)
    }
}