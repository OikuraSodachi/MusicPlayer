package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.components.receiver.NoisyReceiver
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.LateInitObjects.receiver
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// class MusicService : MediaBrowserServiceCompat()

@AndroidEntryPoint
class MusicService : MediaSessionService(),MediaSession.Callback   {
    companion object{
        lateinit var serviceIntent : Intent

        /** to be removed **/
        lateinit var customPlayer: CustomPlayer

        /*
        /** to be removed **/
        lateinit var mediaSession: MyMediaSession

         */

        /** temporary getter for player instance. to be removed later. **/
        lateinit var temp_player_getter:ExoPlayer

        lateinit var newMediaSession:MediaSession
        lateinit var newMediaPlayer: ExoPlayer

    }
    private lateinit var notifications: Notifications
    private lateinit var notificationManager:NotificationManagerCompat
    private lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    private val noisyReceiver = NoisyReceiver()
    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val serviceChannel = NotificationChannel(
        Constants.CHANNEL_ID,
        getString(R.string.notification_channel_name),
        NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
    )

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var musicRepo:MusicRepository

    @Inject
    lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true

        notificationManager = NotificationManagerCompat.from(this@MusicService)
        audioFocusChangeListener = MyAudioFocusChangeListener(customPlayer)
        receiver = MusicReceiver()

        /** init newMediaPlayer**/
        newMediaPlayer = ExoPlayer.Builder(this)
            .build()

        /** init newMediaSession **/
        newMediaSession = MediaSession.Builder(this, newMediaPlayer)
            .build()

        notifications = Notifications(this,Constants.CHANNEL_ID)

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)
        registerReceiver(noisyReceiver,noisyIntentFilter, RECEIVER_NOT_EXPORTED)

        customPlayer.initAttributes(this@MusicService)

        customPlayer.apply{
            currentMusicHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isShuffledHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isLoopingHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isPlayingHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
        }       // observe LiveData
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        newMediaSession.player.prepare()
        return newMediaSession
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val updatedMediaItems = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
        return Futures.immediateFuture(updatedMediaItems)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager.createNotificationChannel(serviceChannel)

        //  MediaButtonReceiver.handleIntent(mediaSession,intent)                // 일단 이건 지우지 말고 Keep
        // -> mediaSession: android.support.v4.media.session.MediaSessionCompat  // 일단 이건 지우지 말고 Keep

       /* val notification = mediaSession.noti(this,customPlayer)
        notificationManager.notify(1,notification)
        startForeground(1, notification)
        */   // 일단 이건 지우지 말고 Keep

        val notificationNew = notifications.noti_new(this, newMediaSession)
        notificationManager.notify(1,notificationNew)
        startForeground(1, notificationNew)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        customPlayer.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
       // mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
    }
}