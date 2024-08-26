package com.todokanai.musicplayer.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession

@UnstableApi
class MyActionFactory():MediaNotification.ActionFactory {
    override fun createMediaAction(
        mediaSession: MediaSession,
        icon: IconCompat,
        title: CharSequence,
        command: Int,
    ): NotificationCompat.Action {
        TODO("Not yet implemented")
    }

    override fun createCustomAction(
        mediaSession: MediaSession,
        icon: IconCompat,
        title: CharSequence,
        customAction: String,
        extras: Bundle,
    ): NotificationCompat.Action {
        TODO("Not yet implemented")
    }

    override fun createCustomActionFromCustomCommandButton(
        mediaSession: MediaSession,
        customCommandButton: CommandButton,
    ): NotificationCompat.Action {
        TODO("Not yet implemented")
    }

    override fun createMediaActionPendingIntent(
        mediaSession: MediaSession,
        command: Long,
    ): PendingIntent {
        TODO("Not yet implemented")
    }
}