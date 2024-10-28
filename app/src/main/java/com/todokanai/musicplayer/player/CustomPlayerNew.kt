package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.random.Random

abstract class CustomPlayerNew @Inject constructor(
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository
):CustomPlayer_Holders(dsRepo,musicRepo) {

    val mediaPlayer = MediaPlayer()

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder :StateFlow<Boolean>
        get() = _isPlayingHolder

    fun isShuffled():Boolean = isShuffledHolder.value

    private fun seed() : Double = seedHolder.value

    private fun musicArray() : Array<Music> = musicArrayHolder.value

    fun isLooping() : Boolean = isLoopingHolder.value

    fun getPlayList(
        musicArray:Array<Music> = musicArray(),
        isShuffled:Boolean = isShuffled(),
        seed:Double = seed()
    ):List<Music>{
        return modifiedPlayList(musicArray,isShuffled,seed)
    }

    fun setMusic(music: Music,context: Context,onException:()->Unit){
    //    val isMusicValid = music.fileDir != "empty"
        //if (isMusicValid) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(context, music.getUri())
                setOnCompletionListener {
                    if (!isLooping) {
                        context.sendBroadcast(nextIntent)
                    }
                }
                prepare()
            }
        }catch (e:Exception){
            e.printStackTrace()
            onException()
        }
      //  }
    }

    fun isPlaying_Setter(isPlaying:Boolean){
        _isPlayingHolder.value = isPlaying
    }



    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}