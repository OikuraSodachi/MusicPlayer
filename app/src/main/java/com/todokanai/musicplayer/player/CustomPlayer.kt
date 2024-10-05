package com.todokanai.musicplayer.player

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.MyIcons
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomPlayer(
    val nextIntent:Intent,
    val musicRepo : MusicRepository,
    val dsRepo:DataStoreRepository,
    val stateHolders:PlayerStateHolders
): MediaPlayer() {
    val mediaPlayer = MediaPlayer()

    override fun start() {
        mediaPlayer.start()
        _isPlayingHolder.value = mediaPlayer.isPlaying
    }

    override fun pause() {
        mediaPlayer.pause()
        _isPlayingHolder.value = mediaPlayer.isPlaying
    }

    override fun reset() {
        mediaPlayer.reset()
        _isPlayingHolder.value = mediaPlayer.isPlaying
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
        _isPlayingHolder.value = mediaPlayer.isPlaying
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

    private val _isPlayingHolder = MutableStateFlow(false)
    val isPlayingHolder : StateFlow<Boolean> = _isPlayingHolder

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
    }

    fun pausePlay() =
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
                CoroutineScope(Dispatchers.IO).launch {
                    musicRepo.upsertCurrentMusic(it)
                }
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

    fun launchMusic(context: Context,music: Music){
        this.setMusic(music,context)
        this.start()
    }

    fun prev(context: Context,currentMusic: Music? = currentMusicHolder.value,playList: List<Music> = playListHolder.value){
        currentMusic?.let {
            this.launchMusic(
                context,
                getCircularPrev_td(playList,playList.indexOf(currentMusic))
            )
        }
    }

    fun next(context: Context,currentMusic: Music? = currentMusicHolder.value,playList: List<Music> = playListHolder.value){
        currentMusic?.let {
            this.launchMusic(
                context,
                getCircularNext_td(playList, playList.indexOf(currentMusic))
            )
        }
    }

    fun repeat(){
        val shouldLoop = !mediaPlayer.isLooping
        mediaPlayer.isLooping = shouldLoop
        CoroutineScope(Dispatchers.IO).launch{
            dsRepo.saveIsLooping(shouldLoop)
        }
    }

    fun shuffle(wasShuffled:Boolean = isShuffledHolder.value){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(!wasShuffled)
            dsRepo.saveRandomSeed(seedHolder.value)
        }
    }

    /**
     *  MediaSessionCompat의 PlaybackState Setter
     *
     *  playback position 관련해서는 미검증 상태
     */
    fun MediaSessionCompat.setMediaPlaybackState_td(isLooping :Boolean,isShuffled :Boolean){
        val icons = MyIcons()
        fun state() = if(mediaPlayer.isPlaying){
            PlaybackStateCompat.STATE_PLAYING
        }else{
            PlaybackStateCompat.STATE_PAUSED
        }
        val temp = state()
        fun repeatIcon() = icons.loopingImage(isLooping)
        fun shuffleIcon() = icons.shuffledImage(isShuffled)
        val playbackState = PlaybackStateCompat.Builder()
            .apply {
                val actions = if (temp == PlaybackStateCompat.STATE_PLAYING) {
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
                addCustomAction(Constants.ACTION_REPLAY,"Replay", repeatIcon())
                addCustomAction(Constants.ACTION_SHUFFLE,"Shuffle",shuffleIcon())
                setActions(actions)
            }
            .setState(temp, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }
}