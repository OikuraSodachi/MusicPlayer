package com.todokanai.musicplayer.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class,Music::class,ScanPath::class,CurrentMusic::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase(){

    abstract fun userDao() : UserDao

    abstract fun musicDao() : MusicDao

    abstract fun scanPathDao() : ScanPathDao

    abstract fun currentMusicDao() : CurrentMusicDao

    companion object {
        private var instance: MyDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (instance == null) {
                synchronized(MyDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java,
                        "room_db",
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance!!
        }
    }
}