package com.todokanai.musicplayer.base

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.variables.Variables

abstract class BaseMusicService : MediaBrowserServiceCompat(){

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

    override fun onDestroy() {
        super.onDestroy()
        Variables.isServiceOn = false
    }

    abstract fun prepareMediaSession()
    abstract fun registerReceivers()
}
