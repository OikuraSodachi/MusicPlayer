package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.LateInitObjects.customPlayer
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.repository.ScanPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val spRepository:ScanPathRepository,
    private val musicRepo:MusicRepository,
) : ViewModel() {

    val directoryDialog = MutableStateFlow<Boolean>(false)

    fun directoryDialog(){
        directoryDialog.value = true
    }

    fun directoryDialogOff(){
        directoryDialog.value = false
    }

    /** delete 작업 완료후 insert 진행되는거 확인 완료 **/
    fun apply(context: Context){
        viewModelScope.launch {
            val dirsToScan = spRepository.getPathNonFlow()
            musicRepo.deleteAll()
            scanMusicList(dirsToScan,context).forEach {music ->
                musicRepo.insert(music)
            }
            customPlayer.updatePlayList() // unstable(?)
        }

    }
    //-----------------


    fun deleteScanPath(absolutePath: String){
        viewModelScope.launch {
            spRepository.delete(absolutePath)
        }
    }

    fun clear(){
        viewModelScope.launch {
            spRepository.deleteAll()
            musicRepo.deleteAll()
        }
    }


    val pathList = spRepository.pathList.map{
        it.toList()
    }
    private suspend fun scanMusicList(dirsToScan:List<String>,context: Context): List<Music> = withContext(
        Dispatchers.IO){
        val result = emptyArray<Music>()

        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.MediaColumns.DATA
        )

        val cursor = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,   // URI 값을 주면 나머지 데이터 모아옴
            proj,
            null,
            null,
            null
        )
        val musicList = mutableListOf<Music>()
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getInt(4)
            val fileDir = cursor.getString(5)

            val music = Music(id, title, artist, albumId, duration, fileDir)
            if(dirsToScan.isEmpty()){
                result.plus(music)
                musicList.add(music)
            }else{
                for(a in 1..dirsToScan.size){
                    if(fileDir.startsWith(dirsToScan[a-1])){
                        musicList.add(music)
                    }
                }
            }
        }
        cursor?.close()

        return@withContext musicList.distinct().sortedBy { it.title }
    }



}