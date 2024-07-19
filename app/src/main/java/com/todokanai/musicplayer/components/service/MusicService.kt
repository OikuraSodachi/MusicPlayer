package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.activity.MainActivity
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.components.receiver.NoisyReceiver
import com.todokanai.musicplayer.compose.MyIcons
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.LateInitObjects.customPlayer
import com.todokanai.musicplayer.myobjects.LateInitObjects.mediaSession
import com.todokanai.musicplayer.myobjects.LateInitObjects.receiver
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat()   {
    companion object{
        lateinit var serviceIntent : Intent
    }

    private lateinit var notificationManager:NotificationManagerCompat
    private lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    private val audioManager by lazy{ getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val noisyReceiver = NoisyReceiver()
    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val icons = MyIcons()

    private val mainOpenIntent by lazy{ Intent(this, MainActivity::class.java)}
    private val mainIntent by lazy { PendingIntent.getActivity(this,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)}
    private val repeatIntent by lazy { PendingIntent.getBroadcast(this, 0, Intent(Constants.ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)}
    private val prevIntent by lazy { PendingIntent.getBroadcast(this, 0, Intent(Constants.ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)}
    private val pausePlayIntent by lazy { PendingIntent.getBroadcast(this, 0, Intent(Constants.ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)}
    private val nextIntent by lazy { PendingIntent.getBroadcast(this, 0, Intent(Constants.ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)}
    private val shuffleIntent by lazy { PendingIntent.getBroadcast(this, 0, Intent(Constants.ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)}

    @Inject
    lateinit var musicRepo:MusicRepository

    @Inject
    lateinit var dsRepo:DataStoreRepository

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true

        notificationManager = NotificationManagerCompat.from(this@MusicService)
        audioFocusChangeListener = MyAudioFocusChangeListener(customPlayer)
        receiver = MusicReceiver()

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
        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY))
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS))
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY))
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT))
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE))
        registerReceiver(noisyReceiver,noisyIntentFilter)
        CoroutineScope(Dispatchers.IO).launch {
            customPlayer.initAttributes(this@MusicService,musicRepo.currentMusicNonFlow())
        }

        customPlayer.run{
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val currentMusic = customPlayer.currentMusicHolder.value
        val isLooping = customPlayer.isLoopingHolder.value
        val isShuffled = customPlayer.isShuffledHolder.value
        val isPlaying = customPlayer.isPlayingHolder.value

        val serviceChannel = NotificationChannel(
            Constants.CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        MediaButtonReceiver.handleIntent(mediaSession,intent)

        val title : String = currentMusic?.title ?: "null"
        val artist: String = currentMusic?.artist ?: "null"
        val albumUri : String = currentMusic?.getAlbumUri().toString()

        mediaSession.setMetaData_td(title, artist, albumUri)

        val notification =
            NotificationCompat.Builder(this, Constants.CHANNEL_ID)       // 알림바에 띄울 알림을 만듬
                .setContentTitle("null Title Noti") // 알림의 제목
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(NotificationCompat.Action(icons.loopingImage(isLooping), "REPEAT", repeatIntent))
                .addAction(NotificationCompat.Action(icons.prev,"PREV",prevIntent))
                .addAction(NotificationCompat.Action(icons.pausePlay(isPlaying), "pauseplay", pausePlayIntent))
                .addAction(NotificationCompat.Action(icons.next,"NEXT",nextIntent))
                .addAction(NotificationCompat.Action(icons.shuffledImage(isShuffled), "SHUFFLE", shuffleIntent))
                .setContentIntent(mainIntent)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)     // 확장하지 않은상태 알림에서 쓸 기능의 배열번호
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setOngoing(true)
                .build()
        notificationManager.notify(1,notification)
        startForeground(1, notification)              // 지정된 알림을 실행
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot("MediaSessionExperiment", null)
        }
        return null    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onDestroy() {
        customPlayer.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
    }
}