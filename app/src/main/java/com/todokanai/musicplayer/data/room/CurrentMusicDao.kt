package com.todokanai.musicplayer.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentMusicDao {
    @Query("select * from room_currentmusic where `no`==1")
    fun getCurrentMusic(): Flow<CurrentMusic?>

    @Query("select * from room_currentmusic where `no`==1")
    suspend fun getCurrentNonFlow() : CurrentMusic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: CurrentMusic)

    @Delete
    suspend fun delete(music: CurrentMusic)

    @Query("Delete from room_currentmusic")
    suspend fun deleteAll()
}