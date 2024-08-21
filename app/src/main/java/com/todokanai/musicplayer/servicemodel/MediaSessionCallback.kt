package com.todokanai.musicplayer.servicemodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.MyMediaSession

class MediaSessionCallback(
    val context: Context,
    val mediaSession: MyMediaSession,
    val audioManager:AudioManager,
    val audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener,
    val noisyReceiver:BroadcastReceiver,
    val noisyIntentFilter:IntentFilter,
) : MediaSessionCompat.Callback() {

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
        mediaSession.isActive = true  // 일단 이건 지우지 말고 Keep
        // -> mediaSession: android.support.v4.media.session.MediaSessionCompat  // 일단 이건 지우지 말고 Keep

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
        return super.onMediaButtonEvent(mediaButtonEvent)
    }
    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
    }
    override fun onSetRepeatMode(repeatMode: Int) {
        context.sendBroadcast(Intent(Constants.ACTION_REPLAY))
        super.onSetRepeatMode(repeatMode)
    }
}