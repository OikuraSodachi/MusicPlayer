package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomPlayer (
    private val stateHolders: PlayerStateHolders,
): MediaPlayer(){

    val mediaPlayer = stateHolders.mediaPlayer

    override fun start() {
        mediaPlayer.start()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun pause() {
        mediaPlayer.pause()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun reset() {
        mediaPlayer.reset()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun stop() {
        mediaPlayer.stop()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
        super.stop()
    }
    /** "shuffled" version of ( mediaPlayer.isLooping / mediaPlayer.isPlaying ) **/
    fun isShuffled():Boolean{
        return isShuffledHolder.value
    }

    /** "currentMusic" version of ( mediaPlayer.isLooping / mediaPlayer.isPlaying ) **/
    fun currentMusic():Music{
        return currentMusicHolder.value
    }

    /*
    /** NullPointerException 발생함. 이유는 몰?루 **/
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    */

    // override
    //-------------------------

    val seedHolder = stateHolders.seedHolder

    val currentMusicHolder = stateHolders.currentMusicHolder

    val isLoopingHolder = stateHolders.isLoopingHolder

    val isPlayingHolder = stateHolders.isPlayingHolder

    val isShuffledHolder = stateHolders.isShuffledHolder

    private fun playList() = stateHolders.playList()

    fun initAttributes(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            this.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                stateHolders.onInitAttributes(context)
            }
        }
        // 대충 initial value set
    }

    fun onMusicListScan(context: Context){
        initAttributes(context)
    }

    fun pausePlayAction() =
            if (mediaPlayer.isPlaying) {
                this@CustomPlayer.pause()
            } else {
                this@CustomPlayer.start()
            }

    fun launchMusic(context: Context, music: Music){
        stateHolders.setMusic(context,music)
        this.start()
    }

    fun prevAction(context: Context){
        try {
            val currentMusic = currentMusic()
            val playList = playList()
            this.launchMusic(
                context,
                getCircularPrev_td(playList, playList.indexOf(currentMusic))
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun nextAction(context: Context){
        try {
            val currentMusic = currentMusic()
            val playList = playList()
            this.launchMusic(
                context,
                getCircularNext_td(playList, playList.indexOf(currentMusic))
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun repeatAction(){
        val shouldLoop = !mediaPlayer.isLooping
        mediaPlayer.isLooping = shouldLoop
        stateHolders.setIsLooping(shouldLoop)
    }

    fun shuffleAction() {
        stateHolders.setShuffle(!isShuffled())
    }

    private fun requestUpdateNoti(mediaSession: MediaSessionCompat,startForegroundService:()->Unit){
        mediaSession.setMediaPlaybackState_td(isLooping, isPlaying, isShuffled())
        startForegroundService()
    }

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
           // println("change: isShuffled")
            requestUpdateNoti(mediaSession,startForegroundService)
        }
        /*
        playListHolder.asLiveData().observeForever(){
            println("playList: ${it.map{it.title}}")
        }
         */
    }

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