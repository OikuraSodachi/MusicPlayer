package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.asLiveData
import com.todokanai.musicplayer.base.BaseMusicService
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.repository.MusicState
import com.todokanai.musicplayer.repository.PlayerStateRepository
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
    lateinit var mediaSession:MediaSessionCompat

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var notifications:Notifications

    @Inject
    lateinit var playerStateRepo:PlayerStateRepository

    private val musicStateFlow by lazy{
        playerStateRepo.musicStateFlow.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MusicState()
        )
    }
    override fun onCreate() {
        super.onCreate()
        /** Todo: Flow collect 방식으로 바꿀 것 **/
        musicStateFlow.asLiveData().observeForever{
            CoroutineScope(Dispatchers.IO).launch {
                player.requestUpdateNoti(mediaSession, { startService(serviceIntent(this@MusicService)) })
            }
        }
        dsRepo.isShuffled.asLiveData().observeForever{
            println("dsRepo: $it")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = musicStateFlow.value
        notifications.updateNotification(
            service = this,
            intent = intent,
            serviceChannel = serviceChannel,
            isPlaying = state.isPlaying,
            isLooping = state.isLooping,
            isShuffled = state.isShuffled,
            currentMusic = state.currentMusic
        )
        println("onStartCommand: ${state.currentMusic.title}, isPlaying: ${state.isPlaying}, isLooping: ${state.isLooping}")
        return super.onStartCommand(intent, flags, startId)
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

    /** Todo: parameter 로 mediaSessionCallback 을 주입하는 방식 고려해볼 것 **/
    override fun prepareMediaSession() {
        sessionToken = mediaSession.sessionToken
        val mediaButtonEnabled = dsRepo.isMediaButtonEnabled.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

        val mediaSessionCallback = MediaSessionCallback(
            this@MusicService,
            audioManager,
            audioFocusChangeListener,
            {mediaButtonEnabled.value}
        )

        mediaSession.apply {
            setCallback(mediaSessionCallback)
            isActive = true
        }
    }
}
