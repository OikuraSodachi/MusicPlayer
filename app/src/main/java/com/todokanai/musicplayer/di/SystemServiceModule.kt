package com.todokanai.musicplayer.di

import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class SystemServiceModule {

    @Provides
    fun provideAudioManager(@ApplicationContext context: Context):AudioManager{
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context):NotificationManagerCompat{
        return NotificationManagerCompat.from(context)
    }
}