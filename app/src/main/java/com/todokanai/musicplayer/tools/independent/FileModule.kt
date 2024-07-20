package com.todokanai.musicplayer.tools.independent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.io.File

/** 파일탐색기 기능을 위한 class **/
class FileModule(defaultPath:File) {




    /** 현재 보고있는 Directory **/
    private val _currentPath = MutableStateFlow<File>(defaultPath)
    val currentPath : StateFlow<File>
        get() = _currentPath

    /** directory tree **/
    val dirTree = currentPath.map { file ->
        file.dirTree()
    }

    val files = currentPath.map{
        it.listFiles() ?: emptyArray()
    }

    /** setter for currentPath **/
    fun updateCurrentPath(file: File){
        _currentPath.value = file
    }

    /** Todokanai
     *
     *  == File.dirTree_td()
     * */
    private fun File.dirTree(): List<File> {
        val result = mutableListOf<File>()
        var now = this
        while (now.parentFile != null) {
            result.add(now)
            now = now.parentFile!!
        }
        return result.reversed()
    }
}