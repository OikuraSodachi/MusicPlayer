package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.Getters.getPlayer
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.PlayerStateHolders
import com.todokanai.musicplayer.player.PlayerStateHolders.Companion.initialMusic
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){

    companion object{
        val serviceIntent = Intent(appContext,MusicService::class.java)
        lateinit var customPlayer: CustomPlayer
        lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    }
    private val notifications = Notifications(Constants.CHANNEL_ID)
    //private lateinit var playerStateHolders: PlayerStateHolders

    private val player by lazy{getPlayer}

    private val receiver by lazy{MusicReceiver()}
    private val serviceChannel by lazy {
        NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
    }

    @Inject
    lateinit var musicRepo:MusicRepository

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var audioManager: AudioManager

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var playerStateHolders: PlayerStateHolders

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        setLateinits()

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)

        player.apply{
            initAttributes(
                initialMusic,
                this@MusicService
            )

            /** case 1**/
            beginObserve(mediaSession,{startForegroundService(serviceIntent)})

            /** case 2 **/
           // beginObserve2(mediaSession,{startForegroundService(serviceIntent)})
            // Todo: 어느 쪽이 더 나은 방식인지?
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
        updateNotification(
            context = this,
            notificationManager = notificationManager,
            intent = intent,
            serviceChannel = serviceChannel,
            mediaSession = mediaSession,
            isPlaying = player.isPlayingHolder.value,
            isLooping = player.isLoopingHolder.value,
            isShuffled = player.isShuffledHolder.value,
            currentMusic = player.currentMusicHolder.value
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        player.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
    }

    private fun updateNotification(
        context:Context,
        notificationManager:NotificationManagerCompat,
        intent:Intent?,
        serviceChannel:NotificationChannel,
        mediaSession:MediaSessionCompat,
        isPlaying:Boolean,
        isLooping:Boolean,
        isShuffled:Boolean,
        currentMusic:Music
    ){
        notificationManager.createNotificationChannel(serviceChannel)
        MediaButtonReceiver.handleIntent(mediaSession,intent)

        val notification = notifications.noti(
            context = context,
            mediaSession = mediaSession,
            isPlaying = isPlaying,
            isLooping = isLooping,
            isShuffled = isShuffled,
            currentMusic = currentMusic,
            repeatIntent = repeatIntent,
            prevIntent = prevIntent,
            pausePlayIntent = pausePlayIntent,
            nextIntent = nextIntent,
            shuffleIntent = shuffleIntent
        )

        notificationManager.notify(1,notification)
        startForeground(1, notification)
    }

    fun setLateinits(){
        /*
        playerStateHolders = PlayerStateHolders(
            musicRepo,
            dsRepo,
            dummyMusic
        )
         */

        customPlayer = CustomPlayer(
            playerStateHolders,
            nextIntent
        )
        audioFocusChangeListener = MyAudioFocusChangeListener(player)
        mediaSession.apply {
            setCallback(
                MediaSessionCallback(
                    this@MusicService,
                    this,
                    audioManager,
                    audioFocusChangeListener
                )
            )
            this@MusicService.sessionToken = sessionToken
        }
    }
}