package com.todokanai.musicplayer.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.activity.MainActivity.Companion.mainIntent
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent
import com.todokanai.musicplayer.variables.Variables.Companion.isTestBuild
import javax.inject.Inject

class Notifications @Inject constructor(
    private val icons : IconsRepository,
    private val channelID:String = Constants.CHANNEL_ID
) {

    private fun noti(
        context: Context,
        isPlaying:Boolean,
        isLooping:Boolean,
        isShuffled:Boolean,
        mainIntent:PendingIntent,
        currentMusic: Music?,
        repeatIntent: Intent,
        prevIntent: Intent,
        pausePlayIntent:Intent,
        nextIntent:Intent,
        shuffleIntent:Intent,
        mediaSession: MediaSessionCompat
    ): Notification {

        if(isTestBuild) {
            println("current: ${currentMusic?.title ?: context.getString(R.string.null_title)}")
            println("isLooping: $isLooping")
        }

        return NotificationCompat.Builder(context, channelID)       // 알림바에 띄울 알림을 만듬
            .setContentTitle("null Title Noti") // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)        //  Todo : 이거 아마 VISIBILITY_PRIVATE 으로 바꿔야 할지도?
            //----------------------------------------------------------------------------------------------------------\
            /**API level < 33 일 때만 적용되는 구간 **/
            .addAction(NotificationCompat.Action(icons.loopingImage(isLooping), "REPEAT", PendingIntent.getBroadcast(context,0,repeatIntent,FLAG_IMMUTABLE)))
            .addAction(NotificationCompat.Action(icons.prev,"PREV",PendingIntent.getBroadcast(context,0,prevIntent,FLAG_IMMUTABLE)))
            .addAction(NotificationCompat.Action(icons.pausePlay(isPlaying), "pauseplay",PendingIntent.getBroadcast(context,0,pausePlayIntent,FLAG_IMMUTABLE)))
            .addAction(NotificationCompat.Action(icons.next,"NEXT",PendingIntent.getBroadcast(context,0,nextIntent,FLAG_IMMUTABLE)))
            .addAction(NotificationCompat.Action(icons.shuffledImage(isShuffled), "SHUFFLE", PendingIntent.getBroadcast(context,0,shuffleIntent,FLAG_IMMUTABLE)))
            //
            //----------------------------------------------------------------------------------------------------------
            .setContentIntent(mainIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)     // 확장하지 않은상태 알림에서 쓸 기능의 배열번호
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setOngoing(true)
            .build()
    }

    fun updateNotification(
        service:MediaBrowserServiceCompat,
        intent:Intent?,
        serviceChannel: NotificationChannel,
        isPlaying:Boolean,
        isLooping:Boolean,
        isShuffled:Boolean,
        currentMusic:Music,
        mediaSession: MediaSessionCompat,
        notificationManager: NotificationManagerCompat
    ): Notification {
        notificationManager.createNotificationChannel(serviceChannel)
        MediaButtonReceiver.handleIntent(mediaSession,intent)
        return noti(
            context = service,
            isPlaying = isPlaying,
            isLooping = isLooping,
            isShuffled = isShuffled,
            currentMusic = currentMusic,
            mainIntent = mainIntent(service),
            repeatIntent = repeatIntent,
            prevIntent = prevIntent,
            pausePlayIntent = pausePlayIntent,
            nextIntent = nextIntent,
            shuffleIntent = shuffleIntent,
            mediaSession = mediaSession
        )
    }
}