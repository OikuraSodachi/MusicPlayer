package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.repository.PlayerStateRepository

/** 값 적용 먼저 하고, holder 에 결과를 반영하는 구조
 * Todo: 값 저장은 여기서는 다루지 말 것.
 *  Todo: start, pause, reset 등 메소드에 MediaPlaybackState setter 를 포함시켜야 할지도? **/
abstract class BasicPlayer(val playerStateRepo: PlayerStateRepository) : MediaPlayer() {

    override fun start() {
        super.start()
        playerStateRepo.onStart(isPlaying)
    }

    override fun pause() {
        super.pause()
        playerStateRepo.onPause(isPlaying)
    }

    override fun stop() {
        super.stop()
        playerStateRepo.onStop(isPlaying)
    }

    override fun setLooping(p0: Boolean) {
        super.setLooping(p0)
        playerStateRepo.onSetLooping(isLooping)
    }
}