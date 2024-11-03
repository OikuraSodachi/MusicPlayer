package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.repository.ScanPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val spRepository:ScanPathRepository,
    private val musicRepo:MusicRepository,
    private val player: CustomPlayer
) : ViewModel() {

    /** 더 깔끔한 IO 작업 중복 방지 방식은 없는지 고민해볼 것 **/
    fun apply(context: Context,button: View){
        CoroutineScope(Dispatchers.IO).launch {
            button.isClickable = false
            val dirsToScan = spRepository.getPathNonFlow()
            val newList  = scanMusicList(dirsToScan,context).toTypedArray()
            musicRepo.updateMusicList(newList)
            player.onMusicListScan(context)
        }.invokeOnCompletion {
            button.isClickable = true
        }
    }
    //-----------------

    fun deleteScanPath(absolutePath: String){
        CoroutineScope(Dispatchers.IO).launch {
            spRepository.delete(absolutePath)
        }
    }

    val pathList = spRepository.paths.map{
        it.toList()
    }

    private suspend fun scanMusicList(dirsToScan:Array<String>,context: Context): Set<Music> = withContext(
        Dispatchers.IO){
        var result = emptySet<Music>()
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
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getInt(4)
            val fileDir = cursor.getString(5)

            val music = Music(id, title, artist, albumId, duration, fileDir)
            if(dirsToScan.isEmpty()){
                result = result.plus(music)

            }else{
                for(a in 1..dirsToScan.size){
                    if(fileDir.startsWith(dirsToScan[a-1])){
                        result = result.plus(music)
                    }
                }
            }
        }
        cursor?.close()
        return@withContext result
    }

    private suspend fun scanMediaItemList(dirsToScan: Array<String>, context: Context):Set<MediaItem> = withContext(Dispatchers.Main
    ){
        var result = emptySet<MediaItem>()
        val proj = arrayOf(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString(),
            MediaStore.MediaColumns.DATA
        )

        val cursor = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            proj,
            null,
            null,
            null
        )

        while (cursor?.moveToNext() == true) {
            val uri = cursor.getString(0)
            val fileDir = cursor.getString(1)

            val music = MediaItem.fromUri(uri)
            if(dirsToScan.isEmpty()){
                result = result.plus(music)

            }else{
                for(a in 1..dirsToScan.size){
                    if(fileDir.startsWith(dirsToScan[a-1])){
                        result = result.plus(music)
                    }
                }
            }
        }
        cursor?.close()
        return@withContext result
    }
}