package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.room.ScanPath
import com.todokanai.musicplayer.data.room.ScanPathDao
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanPathRepository @Inject constructor(private val scanPathDao:ScanPathDao){

    /** scanPathList에서 absolutePath만 추출한 값 **/
    val pathList = scanPathDao.getPath()

    val pathListNew = scanPathDao.getAll()

    val pathListTest = pathListNew.map {
        it.map { it.absolutePath }
    }

    suspend fun getAllNonFlow() = scanPathDao.getAllNonFlow()

    /** 중복여부 check 후, insert **/
    suspend fun insert(file: File){
        val scanPath = ScanPath(file.absolutePath)
        if(!getAllNonFlow().contains(scanPath)){
            insertScanPath(scanPath)
        }
    }
    suspend fun getPathNonFlow() = scanPathDao.getPathNonFlow()

    private suspend fun insertScanPath(scanPath: ScanPath) = scanPathDao.insert(scanPath)

    suspend fun deleteAll() = scanPathDao.deleteAll()

    suspend fun delete(absolutePath:String) = deleteScanPath(absolutePath)

    private suspend fun deleteScanPath(scanPath: String) = scanPathDao.delete(scanPath)


}