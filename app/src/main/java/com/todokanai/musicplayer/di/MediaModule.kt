package com.todokanai.musicplayer.di

import android.content.Context
import android.media.AudioAttributes
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.repository.PlayListRepository
import com.todokanai.musicplayer.repository.PlayerStateRepository
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MediaModule {

//    @Singleton
//    @Provides
//    fun provideMediaPlayer():MediaPlayer{
//        return MediaPlayer().apply {
//            this.apply {
//                setAudioAttributes(
//                    AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .build()
//                )
//            }
//        }
//    }

    @Singleton
    @Provides
    fun provideNewPlayer(mediaSession:MediaSessionCompat,playListRepository: PlayListRepository):NewPlayer{
        return NewPlayer(mediaSession,playListRepository).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    @Singleton
    @Provides
    fun provideMyAudioFocusChangeListener(player: NewPlayer):MyAudioFocusChangeListener{
        return MyAudioFocusChangeListener(player)
    }

    @Singleton
    @Provides
    fun provideMediaSession(@ApplicationContext context:Context):MediaSessionCompat{
        return MediaSessionCompat(context, Constants.MEDIA_SESSION_TAG)
    }

    @Singleton
    @Provides
    fun providePlayerStateRepository(dataStoreRepository: DataStoreRepository, playListRepository: PlayListRepository):PlayerStateRepository{
        return PlayerStateRepository(dataStoreRepository,playListRepository)
    }

    @Singleton
    @Provides
    fun provideNotifications(notificationManager:NotificationManagerCompat,mediaSession:MediaSessionCompat,icons:IconsRepository):Notifications{
        return Notifications(notificationManager,mediaSession,icons)
    }
}