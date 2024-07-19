package com.todokanai.musicplayer.tools

import com.todokanai.musicplayer.myobjects.Constants.BY_DATE_ASCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_DATE_DESCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_DEFAULT
import com.todokanai.musicplayer.myobjects.Constants.BY_NAME_ASCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_NAME_DESCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_SIZE_ASCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_SIZE_DESCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_TYPE_ASCENDING
import com.todokanai.musicplayer.myobjects.Constants.BY_TYPE_DESCENDING
import java.io.File

class FileListSorter() {

    companion object{
        private val sortModeListOriginal = arrayOf(
            BY_DEFAULT,
            BY_NAME_ASCENDING,
            BY_NAME_DESCENDING,
            BY_SIZE_ASCENDING,
            BY_SIZE_DESCENDING,
            BY_TYPE_ASCENDING,
            BY_TYPE_DESCENDING,
            BY_DATE_ASCENDING,
            BY_DATE_DESCENDING
        )
    }

    fun getSortModeCallbackList(callback:(text:String)->Unit):List<Pair<String,()->Unit>>{
        val result = mutableListOf<Pair<String,()->Unit>>()
        sortModeListOriginal.forEach { text ->
            result.add(Pair(text,{callback(text)}))
        }
        return result
    }

    /** sort 적용된 fileList를 반환 */
    fun sortFileList(sortMode:String, files:Array<File>):List<File>{
        return when(sortMode){
            BY_DEFAULT ->{
                files.sortedWith (
                    compareBy({it.isFile},{it.name})
                )
            }
            BY_NAME_ASCENDING ->{
                files.sortedBy{it.name}
            }
            BY_NAME_DESCENDING ->{
                files.sortedByDescending{it.name}
            }
            BY_SIZE_ASCENDING ->{
                files.sortedBy{it.getTotalSize()}
            }
            BY_SIZE_DESCENDING ->{
                files.sortedByDescending { it.getTotalSize() }
            }
            BY_TYPE_ASCENDING ->{
                files.sortedBy{it.extension}
            }
            BY_TYPE_DESCENDING ->{
                files.sortedByDescending { it.extension }
            }
            BY_DATE_ASCENDING ->{
                files.sortedBy{it.lastModified()}
            }
            BY_DATE_DESCENDING ->{
                files.sortedByDescending { it.lastModified() }
            } else -> {
                files.toList()
            }
        }
    }

    /** 하위 디렉토리 포함한 크기 */
    private fun File.getTotalSize(): Long {
        var size: Long = 0
        if(this.isDirectory) {
            val files = this.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        file.getTotalSize()
                    } else {
                        file.length()
                    }
                }
            }
        } else {
            return this.length()
        }
        return size
    }
}