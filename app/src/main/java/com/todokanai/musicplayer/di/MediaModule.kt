package com.todokanai.musicplayer.di

import android.content.Context
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.player.NewPlayer
import com.todokanai.musicplayer.repository.MusicRepository
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

    @Singleton
    @Provides
    fun provideNewPlayer(@ApplicationContext context: Context,musicRepository: MusicRepository,dsRepo:DataStoreRepository,playerStateRepository: PlayerStateRepository):NewPlayer{
        return NewPlayer(musicRepository,dsRepo,playerStateRepository).apply {
            onInit(context)
        }
    }

    @Singleton
    @Provides
    fun provideMyAudioFocusChangeListener(player: NewPlayer):MyAudioFocusChangeListener{
        return MyAudioFocusChangeListener(player)
    }

    @Singleton
    @Provides
    fun providePlayerStateRepository(dataStoreRepository: DataStoreRepository,musicRepository: MusicRepository):PlayerStateRepository{
        return PlayerStateRepository(dataStoreRepository,musicRepository)
    }

    @Singleton
    @Provides
    fun provideNotifications(icons:IconsRepository):Notifications{
        return Notifications(icons)
    }

    /** experimental feature **/
    @Singleton
    @Provides
    fun providePlayerInterface(newPlayer: NewPlayer):PlayerInterface{
        return newPlayer
    }
}