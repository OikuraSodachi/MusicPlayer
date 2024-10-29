package com.todokanai.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

abstract class CustomPlayerNew (
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository
):CustomPlayer_Holders(dsRepo,musicRepo) {

    val mediaPlayer = MediaPlayer()

    val _isPlayingHolder = MutableStateFlow<Boolean>(false)
   // val isPlayingHolder :StateFlow<Boolean>
     //   get() = _isPlayingHolder

    private fun isShuffled():Boolean = _isShuffledHolder.value

    private fun seed() : Double = _seedHolder.value

    private fun musicArray() : Array<Music> = musicArrayHolder.value


    fun isPlaying_Setter(isPlaying:Boolean){
        _isPlayingHolder.value = isPlaying
    }

    fun getPlayList(
        musicArray:Array<Music> = musicArray(),
        isShuffled:Boolean = isShuffled(),
        seed:Double = seed()
    ):List<Music>{
        return modifiedPlayList(musicArray,isShuffled,seed)
    }

    private fun setMusicPrimitive(music: Music,context: Context,onException:()->Unit = {}){
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

    fun setMusic(music: Music,context: Context,playList :List<Music> = getPlayList()){
        var i = 0
        try {
            setMusicPrimitive(
                music = music,
                context = context
            )
        } catch(e:Exception){
            println(e.stackTrace)
            i++
            if(i!=playList.size) {
                setMusicPrimitive(
                    music = getCircularNext_td(playList, playList.indexOf(music)),
                    context = context
                )
            } else{
                Toast.makeText(context,context.getString(R.string.all_item_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun start(){
        mediaPlayer.start()
        isPlaying_Setter(mediaPlayer.isPlaying)
    }

    fun launchMusic(context: Context,music: Music){
        setMusic(music, context)
        start()
    }





    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}