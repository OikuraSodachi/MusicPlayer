package com.todokanai.musicplayer.components.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.base.BaseMusicService
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.interfaces.PlayerStateInterface
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.MusicState
import com.todokanai.musicplayer.player.MyMediaSession
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : BaseMusicService(){

    companion object{
        fun serviceIntent(context: Context) = Intent(context, MusicService::class.java)
    }

    @Inject
    lateinit var player: NewPlayer

    private val receiver by lazy{MusicReceiver()}
    private val serviceChannel by lazy {
        NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
    }

    @Inject
    lateinit var audioManager: AudioManager

    @Inject
    lateinit var audioFocusChangeListener: MyAudioFocusChangeListener

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var notifications:Notifications

    @Inject
    lateinit var playerState: PlayerStateInterface

    @Inject
    lateinit var iconsRepo:IconsRepository

    @Inject
    override lateinit var notificationManager:NotificationManagerCompat

    private val mediaSession by lazy{ MyMediaSession.getInstance(this) }

    private val musicStateFlow by lazy{
        playerState.musicStateFlow.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MusicState()
        )
    }

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


    override fun onCreate() {
        super.onCreate()
        /** Todo: Flow collect 방식으로 바꿀 것 **/
        musicStateFlow.asLiveData().observeForever{
            CoroutineScope(Dispatchers.IO).launch {
                mediaSession.setMediaPlaybackState_td(
                    iconsRepo.loopingImage(it.isLooping),
                    it.isPlaying,
                    iconsRepo.shuffledImage(it.isShuffled)
                )         // notification 에 업데이트 할 값들 준비

                startService(serviceIntent(this@MusicService))     // onStartCommand 호출 유발 지점
            }
        }
    }

    override fun onDestroy() {
        player.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        super.onDestroy()
    }

    override fun registerReceivers() {
        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)
    }

    override fun updateNotification(notificationManager: NotificationManagerCompat, intent: Intent?): Notification {
        val state = musicStateFlow.value
        return notifications.updateNotification(
            service = this,
            intent = intent,
            serviceChannel = serviceChannel,
            isPlaying = state.isPlaying,
            isLooping = state.isLooping,
            isShuffled = state.isShuffled,
            currentMusic = state.currentMusic,
            mediaSession = mediaSession,
            notificationManager = notificationManager
        )
    }

    /** Todo: parameter 로 mediaSessionCallback 을 주입하는 방식 고려해볼 것 **/
    override fun prepareMediaSession() {
        sessionToken = mediaSession.sessionToken
        val mediaButtonEnabled = dsRepo.isMediaButtonEnabled.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

        mediaSession.apply {
            val mediaSessionCallback = MediaSessionCallback(
                this@MusicService,
                audioManager,
                audioFocusChangeListener,
                {mediaButtonEnabled.value}
            )
            setCallback(mediaSessionCallback)
            isActive = true
        }
    }
}
