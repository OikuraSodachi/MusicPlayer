package com.todokanai.musicplayer.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("select * from room_user")
    fun getAll() : Flow<List<User>>

    @Query("select * from room_user")
    suspend fun getAllNonFlow() : List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user : User)

    @Delete
    suspend fun delete(user : User)

    @Query("delete from room_user")
    suspend fun deleteAll()

    @Query("select * from room_user where `no`=:no")
    fun getUserByIndex(no:Long) : Flow<User>

}