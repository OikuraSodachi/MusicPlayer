package com.todokanai.musicplayer.di

import android.media.MediaPlayer
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.interfaces.PlayerStateInterface
import com.todokanai.musicplayer.player.NewPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PlayerModule {

    /** inject NewPlayer as PlayerInterface **/
    @Singleton
    @Provides
    fun providePlayerInterface(newPlayer: NewPlayer): PlayerInterface {
        return newPlayer
    }

    @Singleton
    @Provides
    fun providePlayerStateInterface(newPlayer: NewPlayer): PlayerStateInterface {
        return newPlayer
    }

    /** inject NewPlayer as MediaPlayer **/
    @Singleton
    @Provides
    fun provideMediaPlayer(newPlayer: NewPlayer): MediaPlayer {
        return newPlayer
    }
}