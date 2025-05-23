package com.todokanai.musicplayer.servicemodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.components.receiver.NoisyReceiver
import com.todokanai.musicplayer.myobjects.Constants

class MediaSessionCallback(
    private val context: Context,
    private val audioManager:AudioManager,
    private val audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener,
    val mediaButtonEnabled:()->Boolean
) : MediaSessionCompat.Callback() {

    private val noisyReceiver = NoisyReceiver()
    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private fun isAudioFocusGranted(): Boolean {
        val requestResult = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN)
        return requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun onPlay() {
        onPrepare()
        if (!isAudioFocusGranted()) {
            return
        }
        context.registerReceiver(noisyReceiver, noisyIntentFilter, Context.RECEIVER_NOT_EXPORTED)
        //mediaSession.isActive = true

        context.sendBroadcast(Intent(Constants.ACTION_PAUSE_PLAY))
    }
    override fun onStop() {
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        context.unregisterReceiver(noisyReceiver)
    }
    override fun onPause() {
        context.sendBroadcast(Intent(Constants.ACTION_PAUSE_PLAY))
    }
    override fun onSetShuffleMode(shuffleMode: Int) {
        context.sendBroadcast(Intent(Constants.ACTION_SHUFFLE))
        super.onSetShuffleMode(shuffleMode)
    }
    override fun onSkipToPrevious() {
        context.sendBroadcast(Intent(Constants.ACTION_SKIP_TO_PREVIOUS))
        super.onSkipToPrevious()
    }
    override fun onSkipToNext() {
        context.sendBroadcast(Intent(Constants.ACTION_SKIP_TO_NEXT))
        super.onSkipToNext()
    }
    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        if(mediaButtonEnabled()){
            return super.onMediaButtonEvent(mediaButtonEvent)
        } else{
            println("mediaButton disabled")
            return false
        }
    }
    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
    }
    override fun onSetRepeatMode(repeatMode: Int) {
        context.sendBroadcast(Intent(Constants.ACTION_REPLAY))
        super.onSetRepeatMode(repeatMode)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        when(action){
            Constants.ACTION_REPLAY ->{
                context.sendBroadcast(Intent(Constants.ACTION_REPLAY))
            }
            Constants.ACTION_SHUFFLE ->{
                context.sendBroadcast(Intent(Constants.ACTION_SHUFFLE))
            }
        }
        super.onCustomAction(action, extras)
    }
}