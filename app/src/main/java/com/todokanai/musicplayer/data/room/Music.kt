package com.todokanai.musicplayer.data.room

import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem
import java.text.SimpleDateFormat

@Entity(tableName = "room_music")
data class Music(
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

    private fun durationText():String{
        return if(duration / (1000 * 60 * 60) <= 0){
            SimpleDateFormat("mm:ss").format(duration)
        } else{
            SimpleDateFormat("hh:mm:ss").format(duration)
        }
    }

    fun getAlbumUri(): Uri? {
        return Uri.parse(
            "content://media/external/audio/albumart/$albumId"    //앨범 이미지 주소
        )
    }

    fun toCurrentMusic():CurrentMusic{
        return CurrentMusic(id, title, artist, albumId, duration, fileDir)
    }

    fun toMusicHolderItem():MusicHolderItem{
        return MusicHolderItem(
            this,
            this.title,
            this.artist,
            this.durationText(),
            this.getAlbumUri()
        )
    }

    fun toMediaItem(): MediaItem{
        return MediaItem.fromUri(this.getUri())
    }
}
