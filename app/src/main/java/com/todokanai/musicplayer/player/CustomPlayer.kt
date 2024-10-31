package com.todokanai.musicplayer.player

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td

class CustomPlayer (
    private val stateHolders: PlayerStateHolders,
    private val nextIntent:Intent
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

    val playListHolder = stateHolders.playListHolder


    fun initAttributes(initialMusic:Music?,context: Context) {
        this.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            this.setMusic(initialMusic,context)
        }
        // 대충 initial value set
    }

    fun pausePlayAction() =
            if (mediaPlayer.isPlaying) {
                this@CustomPlayer.pause()
            } else {
                this@CustomPlayer.start()
            }

    /** onCompletion() 의 context에 주의 (?). 아직 미검증 상태  **/
    private fun setMusicPrimitive(music: Music?,context: Context){
        music?.let {
            val shouldLoop = isLoopingHolder.value
            val isMusicValid = music.fileDir != "empty"

            if (isMusicValid) {
                reset()
                mediaPlayer.apply {
                    setDataSource(context, music.getUri())
                    setOnCompletionListener {
                        if (!isLooping) {
                            context.sendBroadcast(nextIntent)
                        }
                    }

                    isLooping = shouldLoop
                    prepare()
                }
                stateHolders.setCurrentMusic(music)
            }
        }
    }

    private fun setMusic(music: Music?,context: Context){
        val playList = playListHolder.value
        var i = 0
        try {
            setMusicPrimitive(
                music = music,
                context = context
            )
        } catch(e:Exception){
            println(e.stackTrace)
            i++
            if(i!=playList.size) {
                setMusicPrimitive(
                    music = getCircularNext_td(playList, playList.indexOf(music)),
                    context = context
                )
            } else{
                Toast.makeText(context,context.getString(R.string.all_item_failure),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchMusic(context: Context, music: Music){
        this.setMusic(music,context)
        this.start()
    }

    fun prevAction(context: Context){
        try {
            val currentMusic = currentMusicHolder.value
            val playList = playListHolder.value
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
            val currentMusic = currentMusicHolder.value
            val playList = playListHolder.value
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
        stateHolders.setShuffle(!isShuffledHolder.value)
    }

    private fun requestUpdateNoti(mediaSession: MediaSessionCompat,startForegroundService:()->Unit){
        mediaSession.setMediaPlaybackState_td(isLoopingHolder.value, isPlayingHolder.value, isShuffledHolder.value)
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
        playListHolder.asLiveData().observeForever(){
            println("playList: ${it.map{it.title}}")
        }
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

data class MusicTest(
    val music:Music,
    val isLooping:Boolean,
    val isShuffled:Boolean,
    val isPlaying:Boolean
)