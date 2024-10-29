package com.todokanai.musicplayer.data.room

import android.net.Uri
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import java.text.SimpleDateFormat

@Entity(tableName = "room_currentmusic")
data class CurrentMusic(
    @ColumnInfo val id: String = dummyMusic.id,
    @ColumnInfo val title: String? = dummyMusic.title,
    @ColumnInfo val artist: String? = dummyMusic.artist,
    @ColumnInfo val albumId: String? = dummyMusic.albumId,
    @ColumnInfo val duration: Int = dummyMusic.duration,
    @ColumnInfo val fileDir: String = dummyMusic.fileDir
){
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var no: Long? = null

    fun getUri(): Uri {
        return Uri.withAppendedPath(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id     // 음원의 주소
        )
    }

    fun durationText():String{
        return if(duration / (1000 * 60 * 60) <= 0){
            SimpleDateFormat("mm:ss").format(duration)
        }  else{
            SimpleDateFormat("hh:mm:ss").format(duration)
        }
    }

    fun getAlbumUri(): Uri? {
        return Uri.parse(
            "content://media/external/audio/albumart/$albumId"    //앨범 이미지 주소
        )
    }

    fun toMusic():Music{
        return Music(id, title, artist, albumId, duration, fileDir)
    }
}
