package com.todokanai.musicplayer.base

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.variables.Variables

abstract class BaseMusicService : MediaBrowserServiceCompat(){

    abstract val notificationManager:NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true

        prepareMediaSession()
        registerReceivers()
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
        val newNotification = updateNotification(notificationManager,intent)
        notificationManager.notify(1,newNotification)
        startForeground(1,newNotification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Variables.isServiceOn = false
    }

    abstract fun prepareMediaSession()
    abstract fun registerReceivers()

    /** @return notification to be updated **/
    abstract fun updateNotification(notificationManager: NotificationManagerCompat,intent:Intent?):Notification
}
