package com.todokanai.musicplayer.interfaces

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.exoplayer.ExoPlayer
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext
import com.todokanai.musicplayer.tools.independent.getCircularPrev
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

interface mediaInterface {

    fun start()

    fun pause()

    fun reset()

    fun isPlaying():Boolean

    fun isLooping():Boolean

    fun release()

    fun updatePlayList()

    fun pausePlay(mediaPlayer: ExoPlayer) =
        if (mediaPlayer.isPlaying) {
            this.pause()
        } else {
            this.start()
        }
    private fun setMusicPrimitive(
        music: Music?,
        context: Context,
        shouldLoop:Boolean,
        mediaPlayer:ExoPlayer,
        musicRepo:MusicRepository,
        nextIntent: Intent
    ){
        music?.let {
            //val shouldLoop = isLoopingHolder.value
            val isMusicValid = music.fileDir != "empty"
            val tempLoop =
                if(shouldLoop){
                    REPEAT_MODE_ONE
                }else{
                    REPEAT_MODE_ALL
                }

            if (isMusicValid) {
                val mediaItem = MediaItem.fromUri(music.getUri())
                reset()
                mediaPlayer.apply {
                    //setDataSource(context, music.getUri())
                    setMediaItem(mediaItem)

                    addListener(
                        object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_ENDED) {
                                    context.sendBroadcast(nextIntent)
                                }
                            }
                        }
                    )

                    /*
                    setOnCompletionListener {
                        if (tempLoop != REPEAT_MODE_ONE) {
                            context.sendBroadcast(nextIntent)
                        }
                    }
                     */
                    repeatMode = tempLoop
                    prepare()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    musicRepo.upsertCurrentMusic(it)
                }
            }
        }
    }
    private fun setMusic(
        music: Music?,
        context: Context,
        shouldLoop:Boolean,
        mediaPlayer:ExoPlayer,
        musicRepo:MusicRepository,
        nextIntent: Intent,
        playList: List<Music>
    ){
        //val playList = playListHolder.value
        var i = 0
        try {
            setMusicPrimitive(
                music = music,
                context = context,
                shouldLoop = shouldLoop,
                mediaPlayer = mediaPlayer,
                musicRepo = musicRepo,
                nextIntent = nextIntent
            )
        } catch(e:Exception){
            println(e.stackTrace)
            i++
            if(i!=playList.size) {
                setMusicPrimitive(
                    music = getCircularNext(playList, playList.indexOf(music)) as Music,
                    context = context,
                    shouldLoop = shouldLoop,
                    mediaPlayer = mediaPlayer,
                    musicRepo = musicRepo,
                    nextIntent = nextIntent
                )
            } else{
                Toast.makeText(context,context.getString(R.string.all_item_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun launchMusic(context: Context,music: Music){

    }

    fun prev(context: Context,currentMusic:Music,playList:List<Music>){
        this.launchMusic(context, getCircularPrev(playList,playList.indexOf(currentMusic)) as Music)
    }

    fun next(context: Context,currentMusic:Music,playList:List<Music>){
        this.launchMusic(context, getCircularNext(playList,playList.indexOf(currentMusic)) as Music)
    }

    fun repeat()

    fun shuffle(wasShuffled:Boolean)

    private fun modifiedPlayList(musicList:List<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}