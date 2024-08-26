package com.todokanai.musicplayer.exoplayer

import androidx.annotation.OptIn
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import com.google.common.collect.ImmutableList

@OptIn(UnstableApi::class)
class Temp {

    fun commandButtons_td(): ImmutableList<CommandButton> {
        var result = mutableListOf<CommandButton>()

        return result as ImmutableList<CommandButton>
    }

    fun actionFactory_td():MediaNotification.ActionFactory{
        var result : MediaNotification.ActionFactory
        result = MyActionFactory()
        return result
    }

    fun callback_td(notificationManager:NotificationManagerCompat):MediaNotification.Provider.Callback{
        var result:MediaNotification.Provider.Callback
        result = MyCallback(notificationManager)
        return result
    }


}