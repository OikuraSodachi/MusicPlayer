package com.todokanai.musicplayer.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.todokanai.musicplayer.player.CustomPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoisyReceiver : BroadcastReceiver() {
//    private val mediaPlayer by lazy{ getPlayer }

    @Inject
    lateinit var mediaPlayer: CustomPlayer

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            if(mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }
}