package com.todokanai.musicplayer.di

import android.content.Context
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.CurrentMusicDao
import com.todokanai.musicplayer.data.room.MusicDao
import com.todokanai.musicplayer.data.room.MyDatabase
import com.todokanai.musicplayer.data.room.ScanPathDao
import com.todokanai.musicplayer.data.room.UserDao
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.repository.PlayListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideMyDatabase(@ApplicationContext context: Context) : MyDatabase {
        return MyDatabase.getInstance(context)
    }

    @Provides
    fun provideMusicDao(myDatabase: MyDatabase) : MusicDao {
        return myDatabase.musicDao()
    }

    @Provides
    fun provideUserDao(myDatabase: MyDatabase) : UserDao{
        return myDatabase.userDao()
    }

    @Provides
    fun provideScanPathDao(myDatabase: MyDatabase) : ScanPathDao{
        return myDatabase.scanPathDao()
    }

    @Provides
    fun provideCurrentMusicDao(myDatabase: MyDatabase) : CurrentMusicDao{
        return myDatabase.currentMusicDao()
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context):DataStoreRepository{
        return DataStoreRepository(context)
    }

    @Singleton
    @Provides
    fun provideMusicRepository(musicDao: MusicDao,dataStoreRepository: DataStoreRepository):MusicRepository{
        return MusicRepository(musicDao,dataStoreRepository)
    }

    @Singleton
    @Provides
    fun providePlayListRepository(dsRepo:DataStoreRepository,musicRepo:MusicRepository):PlayListRepository{
        return PlayListRepository(dsRepo,musicRepo)
    }

    @Singleton
    @Provides
    fun provideIconsRepository():IconsRepository{
        return IconsRepository()
    }
}