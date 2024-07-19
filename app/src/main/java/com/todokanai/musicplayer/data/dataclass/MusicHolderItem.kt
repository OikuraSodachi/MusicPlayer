package com.todokanai.musicplayer.data.dataclass

import android.net.Uri
import com.todokanai.musicplayer.data.room.Music

data class MusicHolderItem(
    val music: Music,
    val title:String?,
    val artist:String?,
    val durationText:String,
    val albumUri: Uri?,
)