package com.todokanai.musicplayer.data.dataclass

import android.net.Uri
import java.io.File

data class FileHolderItem(
    val file : File,
    val name:String,
    val size:String,
    val lastModified:String,
    val thumbnail: Int,
    val uri:Uri,
    val isAsyncImage:Boolean
)