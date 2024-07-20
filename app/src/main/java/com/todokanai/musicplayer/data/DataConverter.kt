package com.todokanai.musicplayer.data

import androidx.core.net.toUri
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.dataclass.FileHolderItem
import com.todokanai.musicplayer.myobjects.MyObjects
import com.todokanai.musicplayer.tools.FileListSorter
import com.todokanai.musicplayer.tools.independent.readableFileSize_td
import java.io.File
import java.text.DateFormat

class DataConverter() {
    private val sorter = FileListSorter()
    private val thumbnailFolder = R.drawable.ic_baseline_folder_24
    private val thumbnailPdf = R.drawable.ic_pdf
    private val thumbnailDefaultFile =R.drawable.ic_baseline_insert_drive_file_24


    /** file의 extension에 따른 기본 thumbnail 값 */
    private fun thumbnail(file: File): Int {
        return if (file.isDirectory) {
            thumbnailFolder
        } else {
            when (file.extension) {
                "pdf" -> { thumbnailPdf }
                else -> { thumbnailDefaultFile }
            }
        }
    }

    fun toFileHolderItem(file: File): FileHolderItem {
        val lastModified = DateFormat.getDateTimeInstance().format(file.lastModified())
        val size =
            if(file.isDirectory) {
                val subFiles = file.listFiles()
                if(subFiles == null){
                    "null"
                }else {
                    "${subFiles.size} 개"
                }
            } else {
                readableFileSize_td(file.length())
            }
        val thumbnail = thumbnail(file)

        return FileHolderItem(file,file.name,size,lastModified,thumbnail,file.toUri(),
            MyObjects.asyncImageExtension.contains(file.extension) )
    }


    fun fileHolderItemList(files:Array<File>, sortBy:String):List<FileHolderItem>{
        val result = mutableListOf<FileHolderItem>()
        val sortedFileHolderItemList = sorter.sortFileList(sortBy,files)
        sortedFileHolderItemList.forEach { file ->
            if(file.isDirectory) {
                result.add(toFileHolderItem(file))
            }
        }
        return result
    }
}