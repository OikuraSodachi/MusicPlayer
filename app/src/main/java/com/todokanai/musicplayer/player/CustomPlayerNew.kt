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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

abstract class CustomPlayerNew (
    val dsRepo: DataStoreRepository,
    val musicRepo:MusicRepository
):MediaPlayer() {
  //  val stateHolders = PlayerStateHolders(musicRepo, dsRepo)
    val mediaPlayer = MediaPlayer()

    private val seed = 0.1

    abstract val seedHolder:MutableStateFlow<Double>
    abstract val playListHolder: MutableStateFlow<List<Music>>
    abstract val isPlayingHolder:MutableStateFlow<Boolean>
    abstract val isShuffledHolder:MutableStateFlow<Boolean>
    abstract val isLoopingHolder:MutableStateFlow<Boolean>
    abstract val currentMusicHolder:MutableStateFlow<Music>
    abstract val musicArray : MutableStateFlow<Array<Music>>
 //   private var musicArray = emptyArray<Music>()

    fun getPlayList():List<Music>{
        return playListHolder.value
     //   return modifiedPlayList(stateHolders.playListHolder.value,isShuffled,seed)
    }

    fun getCurrentMusic():Music{
        return currentMusicHolder.value
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
        return isShuffledHolder.value
    }

    fun setIsShuffled(shuffle:Boolean){
        isShuffledHolder.value = shuffle
        saveShuffle(shuffle)
    }


    override fun start() {
        mediaPlayer.start()
        isPlayingHolder.value = isPlaying
    }

    override fun pause() {
        mediaPlayer.pause()
        isPlayingHolder.value = isPlaying
    }

    override fun reset() {
        mediaPlayer.reset()
        isPlayingHolder.value = isPlaying
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun setLooping(p0: Boolean) {
        isLoopingHolder.value = p0
        saveLoop(p0)
        super.setLooping(p0)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun stop() {
        mediaPlayer.stop()
        isPlayingHolder.value = isPlaying
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

    private fun saveShuffle(isShuffled:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private fun saveLoop(isLooping:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private fun saveSeed(seed:Double){
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveSeed(seed)
        }
    }

}