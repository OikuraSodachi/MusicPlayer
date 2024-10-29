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
import kotlinx.coroutines.flow.StateFlow

abstract class CustomPlayerNew (
    override val dsRepo: DataStoreRepository,
    override val musicRepo:MusicRepository
):CustomPlayer_Holders,MediaPlayer() {

    val mediaPlayer = MediaPlayer()

    private val _isPlayingHolder = MutableStateFlow<Boolean>(false)
    val isPlayingHolder : StateFlow<Boolean>
        get() = _isPlayingHolder


    fun _setIsPlaying(isPlaying:Boolean){
        _isPlayingHolder.value = isPlaying
    }
    /*
    fun getPlayList(
        musicArray:Array<Music>,
        isShuffled:Boolean,
        seed:Double
    ):List<Music>{
        return modifiedPlayList(musicArray,isShuffled,seed)
    }

     */


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

    fun setMusic(music: Music,context: Context,playList:List<Music>){
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


    fun launchMusic(context: Context,music: Music,playList: List<Music>){
        setMusic(music, context,playList)
        start()
    }


    override fun start() {
        mediaPlayer.start()
        _setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun pause() {
        mediaPlayer.pause()
        _setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun reset() {
        mediaPlayer.reset()
        _setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun stop() {
        mediaPlayer.stop()
        _setIsPlaying(mediaPlayer.isPlaying)
        super.stop()
    }




}