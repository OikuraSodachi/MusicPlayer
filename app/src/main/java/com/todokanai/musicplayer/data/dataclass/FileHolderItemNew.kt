package com.todokanai.musicplayer.data.dataclass

import java.io.File

data class FileHolderItemNew(
    val file : File,
    val name:String,
    val size:String,
    val lastModified:String,
    val thumbnail: Int,
    //   val thumbnail: ImageBitmap
)
