package com.todokanai.musicplayer.tools

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.activity.MainActivity

class Notifications(context: Context,private val channelID:String) {
    val mainOpenIntent = Intent(context, MainActivity::class.java)
    val mainIntent = PendingIntent.getActivity(context,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)

    private fun NotificationCompat.Builder.defaultAttr():NotificationCompat.Builder{
        this.apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentIntent(mainIntent)
            setOngoing(true)
            setSmallIcon(R.mipmap.ic_launcher_round)
        }
        return this
    }

    /*
    fun noti(context: Context, player: CustomPlayer): Notification {

      //  val mainIntent = PendingIntent.getActivity(context,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
        val repeatIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)
        val prevIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)
        val pausePlayIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)
        val shuffleIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, channelID)       // 알림바에 띄울 알림을 만듬
            .setContentTitle("null Title Noti") // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(NotificationCompat.Action(icons.loopingImage(player.isLoopingHolder.value), "REPEAT", repeatIntent))
            .addAction(NotificationCompat.Action(icons.prev,"PREV",prevIntent))
            .addAction(NotificationCompat.Action(icons.pausePlay(player.isPlayingHolder.value), "pauseplay", pausePlayIntent))
            .addAction(NotificationCompat.Action(icons.next,"NEXT",nextIntent))
            .addAction(NotificationCompat.Action(icons.shuffledImage(player.isShuffledHolder.value), "SHUFFLE", shuffleIntent))
            .setContentIntent(mainIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)     // 확장하지 않은상태 알림에서 쓸 기능의 배열번호
                    .setMediaSession(this.sessionToken)
            )
            .setOngoing(true)
            .build()
    }

     */

    /** 미완성 상태 **/
    @OptIn(UnstableApi::class)
    fun noti_new(context: Context,mediaSession:MediaSession):Notification{
        return NotificationCompat.Builder(context,channelID)
            .defaultAttr()
            .setStyle(
                MediaStyle(mediaSession)
                    .setShowActionsInCompactView(1,2,3)
            )
            .build()
    }
}