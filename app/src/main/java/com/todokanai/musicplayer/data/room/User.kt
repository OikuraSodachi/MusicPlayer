package com.todokanai.musicplayer.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_user")
data class User(
    @ColumnInfo val number : Long?,
    @ColumnInfo val dummy : Int = 0
) {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var no : Long? = null

}