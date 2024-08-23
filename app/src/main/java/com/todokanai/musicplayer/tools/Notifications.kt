package com.todokanai.musicplayer.tools

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.activity.MainActivity
import com.todokanai.musicplayer.compose.MyIcons
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer

class Notifications(context: Context,private val channelID:String) {
    private val icons = MyIcons()
    private val mainOpenIntent = Intent(context, MainActivity::class.java)
    private val mainIntent = PendingIntent.getActivity(context,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
    private val repeatIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)
    private val prevIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)
    private val pausePlayIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)
    private val nextIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)
    private val shuffleIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)

    /**API level < 33 **/
    fun noti(context: Context, player: CustomPlayer, mediaSession:MediaSessionCompat): Notification {
        val currentMusic = player.currentMusicHolder.value
        val looping = player.isLoopingHolder.value
        val shuffled = player.isShuffledHolder.value
        val isPlaying = player.isPlayingHolder.value

        mediaSession.apply{
            setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, currentMusic?.title ?: "null title")
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, currentMusic?.artist ?: "null artist")
                    .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, currentMusic?.getAlbumUri().toString())
                    .build()
            )
        }

        return NotificationCompat.Builder(context, channelID)       // 알림바에 띄울 알림을 만듬
            .setContentTitle("null Title Noti") // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(NotificationCompat.Action(icons.loopingImage(looping), "REPEAT", repeatIntent))
            .addAction(NotificationCompat.Action(icons.prev,"PREV",prevIntent))
            .addAction(NotificationCompat.Action(icons.pausePlay(isPlaying), "pauseplay", pausePlayIntent))
            .addAction(NotificationCompat.Action(icons.next,"NEXT",nextIntent))
            .addAction(NotificationCompat.Action(icons.shuffledImage(shuffled), "SHUFFLE", shuffleIntent))
            .setContentIntent(mainIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)     // 확장하지 않은상태 알림에서 쓸 기능의 배열번호
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setOngoing(true)
            .build()
    }
}