package com.todokanai.musicplayer.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("select * from room_music")
    fun getAll() : Flow<Array<Music>>

    @Query("select * from room_music")
    suspend fun getAllNonFlow() : List<Music>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Delete
    suspend fun delete(music: Music)

    @Query("Delete from room_music")
    suspend fun deleteAll()

}