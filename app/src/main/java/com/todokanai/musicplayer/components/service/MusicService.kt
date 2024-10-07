package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.components.receiver.NoisyReceiver
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.LateInitObjects.receiver
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.PlayerStateHolders
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import com.todokanai.musicplayer.variables.Variables.Companion.isTestBuild
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){
    companion object{
        lateinit var serviceIntent : Intent
        lateinit var customPlayer: CustomPlayer
        lateinit var mediaSession: MediaSessionCompat
        var mSeed by Delegates.notNull<Double>()
        lateinit var mPlayList:List<Music>
        var mCurrentMusic : Music? = null
        var mShuffled by Delegates.notNull<Boolean>()
        var mLoop by Delegates.notNull<Boolean>()
    }
    private lateinit var notifications: Notifications
    private lateinit var notificationManager:NotificationManagerCompat
    private lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    private val noisyReceiver = NoisyReceiver()
    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val serviceChannel by lazy {
        NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
    }

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var musicRepo:MusicRepository

    @Inject
    lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        fun setLateinits(){
            mediaSession = MediaSessionCompat(this, Constants.MEDIA_SESSION_TAG)
            customPlayer = CustomPlayer(
                nextIntent = Intent(Constants.ACTION_SKIP_TO_NEXT),
                musicRepo = musicRepo,
                dsRepo = dsRepo,
                stateHolders = PlayerStateHolders(
                    musicRepo,
                    mSeed,
                    mPlayList,
                    mLoop,
                    mShuffled,
                    dummyMusic
                )
            )

            notificationManager = NotificationManagerCompat.from(this@MusicService)
            receiver = MusicReceiver()
            notifications = Notifications(this,Constants.CHANNEL_ID)
            audioFocusChangeListener = MyAudioFocusChangeListener(customPlayer)
        }
        setLateinits()
        mediaSession.apply {
            setCallback(
                MediaSessionCallback(
                    this@MusicService,
                    this,
                    audioManager,
                    audioFocusChangeListener,
                    noisyReceiver,
                    noisyIntentFilter
                )
            )
            this@MusicService.sessionToken = sessionToken
        }

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)
        registerReceiver(noisyReceiver,noisyIntentFilter, RECEIVER_NOT_EXPORTED)

        fun updateNoti(isLooping: Boolean,isPlaying: Boolean,isShuffled: Boolean){
            mediaSession.setMediaPlaybackState_td(isLooping, isPlaying, isShuffled)
            startForegroundService(serviceIntent)
        }

        customPlayer.apply{
            initAttributes(mCurrentMusic,this@MusicService)
            currentMusicHolder.asLiveData().observeForever(){
                updateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)
            }
            isPlayingHolder.asLiveData().observeForever(){
                updateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }
            isLoopingHolder.asLiveData().observeForever(){
                updateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }
            isShuffledHolder.asLiveData().observeForever(){
                updateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }

            if(isTestBuild) {
                stateHolders.playListHolder.asLiveData().observeForever() {
                    println("list: ${it.map { it.title }}")
                }
            }
        }       // observe LiveData
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot(Constants.BROWSER_ROOT_ID, null)
        }
        return null
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.createNotificationChannel(serviceChannel)
        MediaButtonReceiver.handleIntent(mediaSession,intent)

        val notification = notifications.noti(
            context = this,
            mediaSession = mediaSession,
            isPlaying = customPlayer.isPlayingHolder.value,
            isLooping = customPlayer.isLoopingHolder.value,
            isShuffled = customPlayer.isShuffledHolder.value,
            currentMusic = customPlayer.currentMusicHolder.value
        )

        notificationManager.notify(1,notification)
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        customPlayer.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
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
                addCustomAction(Constants.ACTION_REPLAY,"Repeat", repeatIcon())
                addCustomAction(Constants.ACTION_SHUFFLE,"Shuffle",shuffleIcon())
                setActions(actions)
            }
            .setState(temp, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }

}