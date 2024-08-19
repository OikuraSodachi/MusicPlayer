package com.todokanai.musicplayer.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoisyReceiver : BroadcastReceiver() {
    private val mediaPlayer by lazy{ customPlayer }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            if(mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }
}