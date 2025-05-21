package com.todokanai.musicplayer.data.room

import android.net.Uri
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat

@Entity(tableName = "room_currentmusic")
data class CurrentMusic(
    @ColumnInfo val id: String,
    @ColumnInfo val title: String? = null,
    @ColumnInfo val artist: String? = null,
    @ColumnInfo val albumId: String? = null,
    @ColumnInfo val duration: Int,
    @ColumnInfo val fileDir: String
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
//    companion object{
//        fun fromMusic(music:Music):CurrentMusic{
//            return CurrentMusic(
//                id = music.id,
//                title = music.title,
//                artist = music.artist,
//                albumId = music.albumId,
//                duration = music.duration,
//                fileDir = music.fileDir
//            )
//        }
//    }
}
