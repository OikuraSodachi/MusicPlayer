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
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.Getters.getPlayer
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent
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
        val serviceIntent = Intent(appContext,MusicService::class.java)
        lateinit var customPlayer: CustomPlayer

        var initialSeed by Delegates.notNull<Double>()
        lateinit var initialPlayList:List<Music>
        var initialMusic : Music? = null
        var initialShuffled by Delegates.notNull<Boolean>()
        var initialLoop by Delegates.notNull<Boolean>()
    }

    private val notifications = Notifications(Constants.CHANNEL_ID)

    private lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    private lateinit var playerStateHolders: PlayerStateHolders

    private val player by lazy{getPlayer}
    private val mediaSession by lazy{MediaSessionCompat(this, Constants.MEDIA_SESSION_TAG)}
    private val notificationManager by lazy{NotificationManagerCompat.from(this)}
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
    lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        fun setLateinits(){
            playerStateHolders = PlayerStateHolders(
                musicRepo,
                initialSeed,
                initialPlayList,
                initialLoop,
                initialShuffled,
                dummyMusic
            )
            customPlayer = CustomPlayer(playerStateHolders, nextIntent)
            audioFocusChangeListener = MyAudioFocusChangeListener(player)
        }
        setLateinits()

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

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)
       // registerReceiver(noisyReceiver,noisyIntentFilter, RECEIVER_NOT_EXPORTED)

        fun requestUpdateNoti(isLooping: Boolean, isPlaying: Boolean, isShuffled: Boolean){
            mediaSession.setMediaPlaybackState_td(isLooping, isPlaying, isShuffled)
            startForegroundService(serviceIntent)
        }

        // playerStateObserver.apply ....  Todo: 여기부터. Flow.map 방식으로 변경할 것
        player.apply{
            initAttributes(initialMusic,this@MusicService)
            currentMusicHolder.asLiveData().observeForever(){
                requestUpdateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)
            }
            isPlayingHolder.asLiveData().observeForever(){
                requestUpdateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }
            isLoopingHolder.asLiveData().observeForever(){
                requestUpdateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }
            isShuffledHolder.asLiveData().observeForever(){
                requestUpdateNoti(isLoopingHolder.value,isPlayingHolder.value,isShuffledHolder.value)

            }

            if(isTestBuild) {
                player.playListHolder.asLiveData().observeForever() {
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
}