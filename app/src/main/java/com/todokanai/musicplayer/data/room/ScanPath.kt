package com.todokanai.musicplayer.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

/**
 * ScanPathRepository 바깥에서 보이면 안됨
 */
@Entity(tableName = "room_scanpath")
data class ScanPath(
    @ColumnInfo val absolutePath:String
){
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var no: Long? = null

    fun toFile() = File(absolutePath)
}
