package com.todokanai.musicplayer.variables

import android.os.Environment
import java.io.File

class Variables {
    companion object{
        val defaultStorage: File = Environment.getExternalStorageDirectory()
        var isServiceOn = false
        val isTestBuild = true
    }
}