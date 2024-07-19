package com.todokanai.musicplayer.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanPathDao {
    @Query("select * from room_scanpath")
    fun getAll() : Flow<Array<ScanPath>>

    @Query("select * from room_scanpath")
    suspend fun getAllNonFlow() : List<ScanPath>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scanPath: ScanPath)

    @Query("delete from room_scanpath where absolutePath =:absolutePath ")
    suspend fun delete(absolutePath:String)

    @Query("delete from room_scanpath")
    suspend fun deleteAll()

    @Query("select absolutePath from room_scanpath")
    fun getPath():Flow<Array<String>>

    @Query("select absolutePath from room_scanpath")
    suspend fun getPathNonFlow():List<String>

}