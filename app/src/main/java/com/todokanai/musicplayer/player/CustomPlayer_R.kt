package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import javax.inject.Inject

class CustomPlayer_R @Inject constructor(
    musicRepo:MusicRepository,
    dsRepo:DataStoreRepository,
):CustomPlayerNew(dsRepo,musicRepo){

  //  val seedHolder = stateHolders.seedHolder

    val currentMusicHolder = stateHolders.currentMusicHolder

    val isLoopingHolder = stateHolders.isLoopingHolder

    val isPlayingHolder = stateHolders.isPlayingHolder

    val isShuffledHolder = stateHolders.isShuffledHolder

    val playListHolder = stateHolders.playListHolder


    fun initAttributes(initialMusic:Music,context: Context) {
        val playList = getPlayList()
        this.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            this.setMusic(initialMusic,context,playList)
        }
        // 대충 initial value set
    }

    fun pausePlayAction() =
        if (isPlaying) {
            this@CustomPlayer_R.pause()
        } else {
            this@CustomPlayer_R.start()
        }

    fun prevAction(context: Context){
        val currentMusic = getCurrentMusic()
        val playList = getPlayList()

        try {
            this.launchMusic(
                context,
                getCircularPrev_td(playList, playList.indexOf(currentMusic)),
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun nextAction(context: Context){
        val currentMusic = getCurrentMusic()
        val playList = getPlayList()
        try {
            this.launchMusic(
                context,
                getCircularNext_td(playList, playList.indexOf(currentMusic)),
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun repeatAction(){
        val shouldLoop = !isLooping
        this.isLooping = shouldLoop
    }

    fun shuffleAction() {
        setIsShuffled(!getIsShuffled())
    }

    /*
    private fun requestUpdateNoti(mediaSession: MediaSessionCompat,startForegroundService:()->Unit){
        mediaSession.setMediaPlaybackState_td(isLoopingHolder.value, isPlayingHolder.value, isShuffledHolder.value)
        startForegroundService()
    }

     */
    /*
    fun beginObserve(mediaSession: MediaSessionCompat,startForegroundService: () -> Unit){
        currentMusicHolder.asLiveData().observeForever(){
            //  println("change: currentMusic")
            requestUpdateNoti(mediaSession,startForegroundService)
        }
        isPlayingHolder.asLiveData().observeForever(){
            //  println("change: isPlaying")
            requestUpdateNoti(mediaSession,startForegroundService)
        }
        isLoopingHolder.asLiveData().observeForever(){
            //  println("change: isLooping")
            requestUpdateNoti(mediaSession,startForegroundService)
        }
        isShuffledHolder.asLiveData().observeForever(){
            //  println("change: isShuffled = ${it}")
            requestUpdateNoti(mediaSession,startForegroundService)
        }
    }

     */


    fun updatePlayList(newList:Array<Music>) = stateHolders.updatePlayList(newList)


    /**
     *  MediaSessionCompat의 PlaybackState Setter
     *
     *  playback position 관련해서는 미검증 상태
     */
    fun MediaSessionCompat.setMediaPlaybackState_td(isLooping:Boolean,isPlaying:Boolean,isShuffled:Boolean){
        val icons = IconsRepository()
        fun state() = if(isPlaying){
            PlaybackStateCompat.STATE_PLAYING
        }else{
            PlaybackStateCompat.STATE_PAUSED
        }
        val state = state()
        fun repeatIcon() = icons.loopingImage(isLooping)
        fun shuffleIcon() = icons.shuffledImage(isShuffled)
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
                addCustomAction(Constants.ACTION_REPLAY,"Repeat", repeatIcon())
                addCustomAction(Constants.ACTION_SHUFFLE,"Shuffle",shuffleIcon())
                setActions(actions)
            }
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }
}
