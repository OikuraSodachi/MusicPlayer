package com.todokanai.musicplayer.tools.independent

/** 이 method들은 독립적으로 사용 가능함 */

import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
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

/** Todokanai */
fun readableFileSize_td(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

/** Todokanai
 *
 * 안드로이드에서만 적용 가능?
 */
fun getPhysicalStorage_td(file: File): String {
    val path = file.absolutePath
    val firstSlashIndex = path.indexOf('/')
    val secondSlashIndex = path.indexOf('/', startIndex = firstSlashIndex + 1)
    val thirdSlashIndex = path.indexOf('/', startIndex = secondSlashIndex + 1)
    return if (thirdSlashIndex > secondSlashIndex) {
        path.substring(secondSlashIndex + 1, thirdSlashIndex)
    } else {
        path.substring(secondSlashIndex + 1)
    }
}

/** files:Array <<File>> 과 하위 경로의 파일의 크기 합 */
fun getTotalSize_td(files: Array<File>): Long {
    var totalSize: Long = 0
    for (file in files) {
        if (file.isDirectory) {
            totalSize += getTotalSize_td(file.listFiles() ?: emptyArray())
        } else {
            totalSize += file.length()
        }
    }
    return totalSize
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
fun getFileAndFoldersNumber_td(files:Array<File>):Int{
    var total = 0
    for (file in files) {
        if (file.isFile) {
            total ++
        } else if (file.isDirectory) {
            total ++
            total += getFileNumber_td(file.listFiles() ?: emptyArray())
        }
    }
    return total
}


/** Todokanai */
fun File.dirTree_td(): List<File> {
    val result = mutableListOf<File>()
    var now = this
    while (now.parent != null) {
        result.add(now)
        now = now.parentFile!!
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
            val subFiles = file.listFiles()
            subFiles?.forEach { childFile ->
                addToZip(childFile, entryName)
            }
        }
    }
    for (file in files) {
        addToZip(file)
    }
    zipOutputStream.close()
}