package com.todokanai.musicplayer.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.service.MusicService.Companion.mCurrentMusic
import com.todokanai.musicplayer.components.service.MusicService.Companion.mLoop
import com.todokanai.musicplayer.components.service.MusicService.Companion.mPlayList
import com.todokanai.musicplayer.components.service.MusicService.Companion.mSeed
import com.todokanai.musicplayer.components.service.MusicService.Companion.mShuffled
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.exit_td
import com.todokanai.musicplayer.tools.independent.isPermissionGranted_td
import com.todokanai.musicplayer.tools.independent.requestPermission_td
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
        intentService:Intent
    ){
        viewModelScope.launch {
            //-------------
            // customPlayer 초기 parameter
            mSeed = dsRepo.getSeed()
            mPlayList = musicRepo.getAllNonFlow()
            mShuffled = dsRepo.isShuffled()
            mCurrentMusic = musicRepo.currentMusicNonFlow()
            mLoop = dsRepo.isLooping()
            //-----------
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
}