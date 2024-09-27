package com.todokanai.filemanager.tools.independent

/** 이 method들은 독립적으로 사용 가능함 */

import java.io.File
import java.io.FileInputStream
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.log10
import kotlin.math.pow

//----------------------
/** Todokanai
 *
 * Function to get the character sequence from after the last instance of File.separatorChar in a path
 * @author Neeyat Lotlikar
 * @param path String path representing the file
 * @return String filename which is the character sequence from after the last instance of File.separatorChar in a path
 * if the path contains the File.separatorChar. Else, the same path.*/
fun getFilenameForPath_td(path: String): String =
    if (!path.contains(File.separatorChar)) path
    else path.subSequence(
        path.lastIndexOf(File.separatorChar) + 1, // Discard the File.separatorChar
        path.length // parameter is used exclusively. Substring produced till n - 1 characters are reached.
    ).toString()


/** Todokanai
 *
 * File.length() 에만 사용할 것 **/
fun Long.readableFileSize_td():String{
    if (this <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        this / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

/** Todokanai **/
fun durationText_td(duration:Int?):String{
    if(duration == null){
        return "null"
    } else if(duration < 3600000){
        return SimpleDateFormat("mm:ss").format(duration)
    }else {
        return SimpleDateFormat("hh:mm:ss").format(duration)
    }
}


/** Todokanai
 *
 *  Array< File > 과 하위 경로의 파일의 크기 합
 *
 *  directory가 아닐 경우는 file 자체의 크기 반환
 * */
fun Array<File>.getTotalSize_td():Long{
    var totalSize: Long = 0
    for (file in this) {
        if (file.isDirectory) {
            totalSize += file.listFiles()?.getTotalSize_td() ?: 0

        } else {
            totalSize += file.length()
        }
    }
    return totalSize
}

/** Todokanai
 *
 * sort 적용된 fileList를 반환
 * */
fun sortedFileList_td(
    files:Array<File>,
    sortMode:String?
):List<File>{
    /** 하위 디렉토리 포함한 크기 */
    fun File.getTotalSize(): Long {
        var size: Long = 0
        if(this.isDirectory) {
            val listFiles = this.listFiles()
            if (listFiles != null) {
                for (file in listFiles) {
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
    return when(sortMode){
        "BY_DEFAULT" ->{
            files.sortedWith (compareBy({it.isFile},{it.name}))
        }
        "BY_NAME_ASCENDING" ->{
            files.sortedBy{it.name}
        }
        "BY_NAME_DESCENDING" ->{
            files.sortedByDescending{it.name}
        }
        "BY_SIZE_ASCENDING" ->{
            files.sortedBy{ it.getTotalSize() }
        }
        "BY_SIZE_DESCENDING" ->{
            files.sortedByDescending { it.getTotalSize() }
        }
        "BY_TYPE_ASCENDING"->{
            files.sortedBy{it.extension}
        }
        "BY_TYPE_DESCENDING" ->{
            files.sortedByDescending { it.extension }
        }
        "BY_DATE_ASCENDING" ->{
            files.sortedBy{it.lastModified()}
        }
        "BY_DATE_DESCENDING" ->{
            files.sortedByDescending { it.lastModified() }
        } else -> {
            println("sortMode value error : $sortMode")
            files.toList()
        }
    }
}

/** Todokanai
 *
 * Directory의 갯수는 포함이 되지 않음 */
fun getFileNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}
/** Todokanai
 *
 * Directory와 File의 총 갯수*/
fun Array<File>.getFileAndFoldersNumber_td():Int{
    var total = 0
    for (file in this) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total ++
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}

/** Todokanai
 *
 *  file의 하위 파일/폴더들을 반환
 *
 *  권한 문제 / IOException 발생시에는 로그찍고나서 emptyArray 반환
 */
fun getFileArray_td(file:File):Array<File>{
    val listFiles = file.listFiles()
    if(listFiles==null){
        println("${file.name}.listFiles() returned null")
        return emptyArray()
    }else{
        return listFiles
    }
}

/** Todokanai
 *
 *  경로의 Tree를 반환
 *
 *   File.walkTopDown() / File.walkBottomUp으로 대체 가능할지도?
 * */
fun dirTree_td(currentPath:File): List<File> {
    val result = mutableListOf<File>()
    var now = currentPath
    while (now.parent != null) {
        result.add(now)
        now = now.parentFile
    }
    return result.reversed()
}

fun zipFileEntrySize_td(file:java.util.zip.ZipFile):Long{
    var result = 0L

    val entries = file.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement() as ZipEntry
        result += entry.size
    }
    return result
}

/** Todokanai */
fun compressFilesRecursivelyToZip_td(files: Array<File>, zipFile: File) {
    val buffer = ByteArray(1024)
    val zipOutputStream = ZipOutputStream(zipFile.outputStream())

    fun addToZip(file: File, parentPath: String = "") {
        val entryName = if (parentPath.isNotEmpty()) "$parentPath/${file.name}" else file.name

        if (file.isFile) {
            val zipEntry = ZipEntry(entryName)
            zipOutputStream.putNextEntry(zipEntry)

            val inputStream = FileInputStream(file)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                zipOutputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            zipOutputStream.closeEntry()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach { childFile ->
                addToZip(childFile, entryName)
            }
        }
    }
    for (file in files) {
        addToZip(file)
    }
    zipOutputStream.close()
    println("파일 압축이 완료되었습니다.")
}

/** Todokanai
 *
 * 경로가 접근 가능할 경우 true 반환
 * **/
fun File.isAccessible_td():Boolean{
    return this.listFiles() != null
}

/**
 * Function to get the MimeType from a filename by comparing it's file extension
 * @author Neeyat Lotlikar
 * @param filename String name of the file. Can also be a path.
 * @return String MimeType */
fun getMimeType_td(filename: String): String = if (filename.lastIndexOf('.') == -1)
    "resource/folder"
else
    when (filename.subSequence(
        filename.lastIndexOf('.'),
        filename.length
    ).toString().lowercase(Locale.ROOT)) {
        ".doc", ".docx" -> "application/msword"
        ".pdf" -> "application/pdf"
        ".ppt", ".pptx" -> "application/vnd.ms-powerpoint"
        ".xls", ".xlsx" -> "application/vnd.ms-excel"
        ".zip", ".rar" -> "application/x-wav"
        ".7z" -> "application/x-7z-compressed"
        ".rtf" -> "application/rtf"
        ".wav", ".mp3", ".m4a", ".ogg", ".oga", ".weba" -> "audio/*"
        ".ogx" -> "application/ogg"
        ".gif" -> "image/gif"
        ".jpg", ".jpeg", ".png", ".bmp" -> "image/*"
        ".csv" -> "text/csv"
        ".m3u8" -> "application/vnd.apple.mpegurl"
        ".txt", ".mht", ".mhtml", ".html" -> "text/plain"
        ".3gp", ".mpg", ".mpeg", ".mpe", ".mp4", ".avi", ".ogv", ".webm" -> "video/*"
        else -> "*/*"
    }

fun copyFiles_Recursive_td(
    selected:Array<File>,
    targetDirectory:File,
    onProgress:(File)->Unit,
    copyOption:CopyOption = StandardCopyOption.REPLACE_EXISTING
){
    for (file in selected) {
        val target = targetDirectory.resolve(file.name)
        if (file.isDirectory) {
            // Create the target directory
            target.mkdirs()
            onProgress(file)
            // Copy the contents of the directory recursively
            copyFiles_Recursive_td(file.listFiles() ?: arrayOf(), target,onProgress, copyOption)
        } else {
            // Copy the file
            Files.copy(file.toPath(), target.toPath(),)
            onProgress(file)
        }
    }
}

fun deleteRecursively_td(
    file: File,
    onDeleteFile:(File)->Unit
){
    try {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (child in files) {
                    deleteRecursively_td(child, onDeleteFile) // 재귀 호출
                }
            }
        }
        file.delete()
    }catch (e:Exception){
        e.printStackTrace()
    }
}