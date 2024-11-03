package com.todokanai.musicplayer.servicemodel

import android.media.AudioManager
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import javax.inject.Inject

/** Singleton 상태 **/
class MyAudioFocusChangeListener() : AudioManager.OnAudioFocusChangeListener{
    @Inject
    lateinit var mediaPlayer: CustomPlayer
    //private val player by lazy{getPlayer}
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Pause playback immediately
                //mediaController.transportControls.pause()
                // Wait 30 seconds before stopping playback
                //handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30))
                mediaPlayer.stop()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer.setVolume(
                    Constants.DUCK_VOLUME,
                    Constants.DUCK_VOLUME
                )
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    mediaPlayer.setVolume(
                        Constants.FULL_VOLUME,
                        Constants.FULL_VOLUME
                    )
                }
            }
        }
    }
}