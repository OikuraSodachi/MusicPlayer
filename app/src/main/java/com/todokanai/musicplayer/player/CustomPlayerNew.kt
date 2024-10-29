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
import kotlin.random.Random

abstract class CustomPlayerNew (
    final override val dsRepo: DataStoreRepository,
    final override val musicRepo:MusicRepository
):CustomPlayer_Holders,MediaPlayer() {
    val stateHolders = PlayerStateHolders(musicRepo, dsRepo)
    val mediaPlayer = MediaPlayer()

    override var isShuffled = false
    private val seed = 0.1

    private var musicArray = emptyArray<Music>()

    fun getPlayList():List<Music>{
        return modifiedPlayList(musicArray,isShuffled,seed)
    }

    fun getCurrentMusic():Music{
        return stateHolders.currentMusicHolder.value
    }

    private fun setMusicPrimitive(music: Music,context: Context,onException:()->Unit = {}){
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


    fun launchMusic(context: Context,music: Music){
        setMusic(music, context,getPlayList())
        start()
    }

    fun getIsShuffled():Boolean{
        return isShuffled
    }

    fun setIsShuffled(shuffle:Boolean){
        isShuffled = shuffle
        stateHolders.setShuffle(shuffle)
    }


    override fun start() {
        mediaPlayer.start()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun pause() {
        mediaPlayer.pause()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun reset() {
        mediaPlayer.reset()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun setLooping(p0: Boolean) {
        saveLoop(p0)
        stateHolders.setIsLooping(p0)
        super.setLooping(p0)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun stop() {
        mediaPlayer.stop()
        stateHolders.setIsPlaying(mediaPlayer.isPlaying)
        super.stop()
    }


    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        println("seed: $seed")
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

}