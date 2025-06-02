package com.todokanai.musicplayer.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoisyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var player: MediaPlayer

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            if(player.isPlaying) {
                player.pause()
            }
        }
    }
}