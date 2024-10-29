package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterfaceNew
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.random.Random

class CustomPlayer_R @Inject constructor(
    musicRepo:MusicRepository,
    dsRepo:DataStoreRepository,
):CustomPlayerNew(dsRepo,musicRepo),MediaInterfaceNew {
    /*
    override var playList = emptyList<Music>()
    override var isLooping = false
    override var isShuffled = false
    override var currentMusic = dummyMusic
    override var isPlaying = false

     */

    override val playListHolder = combine(
        isShuffledHolder,
        musicArrayHolder,
        seedHolder
    ){ isShuffled, musicArray,seed ->
        modifiedPlayList(musicArray,isShuffled,seed)

    }

    override fun repeatAction(isLooping: Boolean) {
        saveLoop(!isLooping)
    }

    override fun prevAction(context: Context, currentMusic: Music,playList:List<Music>) {
        try {
            launchMusic(
                context,
                getCircularPrev_td(playList, playList.indexOf(currentMusic)),
                playList
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pausePlayAction(isPlaying: Boolean) {
        TODO("Not yet implemented")
    }

    override fun nextAction(context: Context, currentMusic: Music,playList: List<Music>) {
        try {
            launchMusic(
                context,
                getCircularNext_td(playList, playList.indexOf(currentMusic)),
                playList
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun shuffleAction(isShuffled: Boolean) {
        saveShuffle(!isShuffled)
    }

    fun initAttributes(initialMusic: Music, context: Context,playList: List<Music>) {
        this.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            this.setMusic(initialMusic, context, playList)
        }
        // 대충 initial value set
    }

    fun beginObserve(mediaSession: MediaSessionCompat, startForegroundService: () -> Unit) {
        currentMusicHolder.asLiveData().observeForever() {
          //  currentMusic = it ?: dummyMusic
            requestUpdateNoti(mediaSession, startForegroundService)
        }
        isPlayingHolder.asLiveData().observeForever() {
           // isPlaying = it
            requestUpdateNoti(mediaSession, startForegroundService)
        }
        isLoopingHolder.asLiveData().observeForever() {
          //  isLooping = it
            requestUpdateNoti(mediaSession, startForegroundService)
        }
        isShuffledHolder.asLiveData().observeForever() {
            //isShuffled = it
            requestUpdateNoti(mediaSession, startForegroundService)
        }
        playListHolder.asLiveData().observeForever(){
           // playList = it
            requestUpdateNoti(mediaSession,startForegroundService)
        }

    }

    private fun isShuffled():Boolean{
        return false
    }
    private fun requestUpdateNoti(
        mediaSession: MediaSessionCompat,
        startForegroundService: () -> Unit,
    ) {
        mediaSession.setMediaPlaybackState_td(isLooping, isPlaying,isShuffled() )
        startForegroundService()
    }

    /**
     *  MediaSessionCompat의 PlaybackState Setter
     *
     *  playback position 관련해서는 미검증 상태
     */
    fun MediaSessionCompat.setMediaPlaybackState_td(
        isLooping: Boolean,
        isPlaying: Boolean,
        isShuffled: Boolean
    ) {
        val icons = IconsRepository()
        fun state() = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
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
                addCustomAction(Constants.ACTION_REPLAY, "Repeat", repeatIcon())
                addCustomAction(Constants.ACTION_SHUFFLE, "Shuffle", shuffleIcon())
                setActions(actions)
            }
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }

    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}
