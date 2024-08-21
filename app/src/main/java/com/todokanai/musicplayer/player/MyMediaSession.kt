package com.todokanai.musicplayer.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.activity.MainActivity
import com.todokanai.musicplayer.compose.MyIcons
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.Constants

class MyMediaSession(appContext: Context, tag:String):MediaSessionCompat(appContext,tag) {

    val icons = MyIcons()
    fun setMediaPlaybackState_td(state:Int){
        val playbackState = PlaybackStateCompat.Builder()
            .apply {
                val actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                } else {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                }
                setActions(actions)
            }
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        this.setPlaybackState(playbackState.build())
    }
    fun setMetaData_td(currentMusic: Music?){
        this.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, currentMusic?.title ?: "null title")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, currentMusic?.artist ?: "null artist")
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, currentMusic?.getAlbumUri().toString())
                .build()
        )
    }
    fun noti(context: Context,player: CustomPlayer): Notification {

        val mainOpenIntent = Intent(context, MainActivity::class.java)
        val mainIntent = PendingIntent.getActivity(context,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
        val repeatIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)
        val prevIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)
        val pausePlayIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)
        val shuffleIntent = PendingIntent.getBroadcast(context, 0, Intent(Constants.ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(context, Constants.CHANNEL_ID)       // 알림바에 띄울 알림을 만듬
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
}