package com.todokanai.musicplayer.di

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.PlayerStateHolders
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
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
    fun providePlayerStateHolders(musicRepo: MusicRepository, dsRepo: DataStoreRepository):PlayerStateHolders{
        return PlayerStateHolders(musicRepo, dsRepo)
    }

    @Provides
    fun provideCustomPlayer(stateHolders: PlayerStateHolders):CustomPlayer{
        return CustomPlayer(stateHolders)
    }
}