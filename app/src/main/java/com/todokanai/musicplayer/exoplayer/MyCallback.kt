package com.todokanai.musicplayer.exoplayer

import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification

@UnstableApi
class MyCallback(notificationManager:NotificationManagerCompat):MediaNotification.Provider.Callback {
    override fun onNotificationChanged(notification: MediaNotification) {
        //notification.notification

        println("${notification.notificationId }")
    }
}