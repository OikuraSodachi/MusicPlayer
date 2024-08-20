package com.todokanai.musicplayer.servicemodel

import android.media.AudioManager
import androidx.media3.exoplayer.ExoPlayer
import com.todokanai.musicplayer.myobjects.Constants

/** Singleton 상태 **/
class MyAudioFocusChangeListener(private val mediaPlayer: ExoPlayer) : AudioManager.OnAudioFocusChangeListener{
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
                mediaPlayer.volume = Constants.DUCK_VOLUME
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!mediaPlayer.isPlaying) {
                    //mediaPlayer.start()
                    mediaPlayer.play()  // Todo: MediaPlayer.start() 와 ExoPlayer.play()가 서로 correspondent 한지 체크 필요
                    mediaPlayer.volume = Constants.FULL_VOLUME
                }
            }
        }
    }
}