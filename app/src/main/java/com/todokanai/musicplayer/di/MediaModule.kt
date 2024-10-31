package com.todokanai.musicplayer.di

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.PlayerAddOn
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class MediaModule {

    @Provides
    fun provideMyAudioFocusChangeListener():MyAudioFocusChangeListener{
        return MyAudioFocusChangeListener()
    }

    @Provides
    fun provideMediaSession(@ApplicationContext context:Context):MediaSessionCompat{
        return MediaSessionCompat(context, Constants.MEDIA_SESSION_TAG)
    }

    @Provides
    fun providesNotifications(notificationManager:NotificationManagerCompat,mediaSession:MediaSessionCompat):Notifications{
        return Notifications(notificationManager,mediaSession)
    }

    @Provides
    fun providePlayerAddOn(player: CustomPlayer):PlayerAddOn{
        return PlayerAddOn(player)
    }
}