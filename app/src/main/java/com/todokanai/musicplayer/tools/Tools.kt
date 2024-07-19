package com.todokanai.musicplayer.tools

import android.content.Context
import android.provider.MediaStore
import com.todokanai.musicplayer.data.room.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Tools {

    suspend fun scanMusicList(dirsToScan:List<String>,context: Context): List<Music> = withContext(Dispatchers.Default){
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