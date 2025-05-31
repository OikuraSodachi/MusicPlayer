package com.todokanai.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.PlayerInterface
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.repository.PlayListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayer @Inject constructor(
    val musicRepo:MusicRepository,
    val dsRepo: DataStoreRepository,
    val playListRepo:PlayListRepository,
    playerStateRepo: PlayerInterface
):BasicPlayer(playerStateRepo) {

    fun repeatAction() {
        isLooping = !isLooping
    }

    fun pausePlayAction() {
        if(!isPlaying){
            start()
        }else{
            pause()
        }
    }

    fun launchMusic(context: Context,music: Music){
        setMusic_td(context, music)
        start()
    }

    fun getPrev():Music = playListRepo.prevMusic()
    fun getNext():Music = playListRepo.nextMusic()
    fun toggleShuffle() = playListRepo.toggleShuffle()

    //---------------------------

    private fun updateMetaData(mediaSession: MediaSessionCompat, title:String, artist:String, uri:String){
        mediaSession.apply {
            setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                    .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, uri)
                    .build()
            )
        }
    }

    private fun setMusic_td(context: Context, music: Music){
        val wasLooping = isLooping
        val isMusicValid = true

        if (isMusicValid) {
            reset()
            setDataSource(context, music.getUri())
            setOnCompletionListener {
                if (!isLooping) {
                    context.sendBroadcast(nextIntent)
                }
            }
            updateMetaData(
                mediaSession = MyMediaSession.getInstance(context),
                title = music.title ?: context.getString(R.string.null_title),
                artist = music.artist ?: context.getString(R.string.null_artist),
                uri = music.getAlbumUri().toString()
            )

            prepare()
            isLooping = wasLooping
            playerStateRepo.setCurrentMusic(music)
        }
    }

    fun onInit(context: Context){
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        CoroutineScope(Dispatchers.IO).launch {
            setMusic_td( context, musicRepo.currentMusic.first())
            isLooping = dsRepo.isLooping()
        }
    }
}