package com.todokanai.musicplayer.servicemodel

import android.media.AudioManager
import android.media.MediaPlayer
import com.todokanai.musicplayer.myobjects.Constants
import javax.inject.Inject

class MyAudioFocusChangeListener @Inject constructor(
    val player: MediaPlayer
) : AudioManager.OnAudioFocusChangeListener{

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Pause playback immediately
                //mediaController.transportControls.pause()
                // Wait 30 seconds before stopping playback
                //handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30))
                player.stop()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player.setVolume(
                    Constants.DUCK_VOLUME,
                    Constants.DUCK_VOLUME
                )
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!player.isPlaying) {
                    player.start()
                    player.setVolume(
                        Constants.FULL_VOLUME,
                        Constants.FULL_VOLUME
                    )
                }
            }
        }
    }
}