package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayerNewWrapper
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){

    companion object{
        val serviceIntent = Intent(appContext,MusicService::class.java)
    }
   /// @Inject
  //  lateinit var player3:CustomPlayer

    @Inject
    lateinit var player: CustomPlayerNewWrapper

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

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        sessionToken = mediaSession.sessionToken
        val mediaButtonEnabled = dsRepo.isMediaButtonEnabled.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = true
        )


        fun mediaButtonEnabled():Boolean{
            return mediaButtonEnabled.value ?: true
        }

        val mediaSessionCallback = MediaSessionCallback(
            this@MusicService,
            audioManager,
            audioFocusChangeListener,
            {mediaButtonEnabled.value ?: true}
        )

        mediaSession.apply {
            setCallback(mediaSessionCallback)
            isActive = true
        }

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)

        player.apply{
            initAttributes(this@MusicService)

            /** case 1**/
         //   beginObserve({startForegroundService(serviceIntent)})

            /** case 2 **/
           // beginObserve2(mediaSession,{startForegroundService(serviceIntent)})
            // Todo: 어느 쪽이 더 나은 방식인지?
        }       // observe LiveData

        /*
        player3.apply {
            initAttributes(this@MusicService)
        }

         */
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
        notifications.updateNotification(
            service = this,
            intent = intent,
            serviceChannel = serviceChannel,
            isPlaying = player.isPlaying,
            isLooping = player.isLooping,
            isShuffled = player.isShuffled(),
            currentMusic = player.currentMusic()
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
}