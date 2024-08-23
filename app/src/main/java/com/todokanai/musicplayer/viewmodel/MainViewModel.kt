package com.todokanai.musicplayer.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import com.todokanai.musicplayer.components.service.MusicService.Companion.mediaSession
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.exit_td
import com.todokanai.musicplayer.tools.independent.isPermissionGranted_td
import com.todokanai.musicplayer.tools.independent.requestPermission_td
import com.todokanai.musicplayer.tools.independent.setMediaPlaybackState_td
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepo : MusicRepository,
    private val dsRepo: DataStoreRepository,
) : ViewModel(){

    private val permission by lazy{
        if(Build.VERSION.SDK_INT <=32){
            arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE)
        } else{
            arrayOf( Manifest.permission.READ_MEDIA_AUDIO)
        }
    }

    fun launchForeground(
        context:Context,
        appContext: Context,
        intentService:Intent
    ){
        viewModelScope.launch {
            mediaSession = MediaSessionCompat(appContext, "MediaSession")
            customPlayer = CustomPlayer(
                nextIntent = Intent(Constants.ACTION_SKIP_TO_NEXT),
                musicRepo = musicRepo,
                dsRepo = dsRepo,
                seed = dsRepo.getSeed(),
                playList = musicRepo.getAllNonFlow(),
                shuffleMode = dsRepo.isShuffled(),
                currentMusic = musicRepo.currentMusicNonFlow(),
                loop = dsRepo.isLooping(),
                setMediaPlaybackState_td = { mediaSession.setMediaPlaybackState_td(it) }
            )
            ContextCompat.startForegroundService(context, intentService)
        }
    }

    fun getPermission(activity: Activity){
        viewModelScope.launch {
            permission.forEach {
                if (!isPermissionGranted_td(activity, it)) {
                    requestPermission_td(
                        activity,
                        arrayOf(it),
                        {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.permission_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }

    fun exit(activity: Activity,serviceIntent:Intent) = exit_td(activity,serviceIntent)

    /** 뒤로가기 버튼 override **/
    fun onBackPressedOverride(activity: ComponentActivity){
        activity.onBackPressedDispatcher.addCallback {
        }
    }
}