package com.todokanai.musicplayer.components.view

import android.widget.SeekBar

class SeekBarListener(
    val getCurrentPosition:()->Int,
    val onProgressChange : (currentPosition:Int)->Unit,
    val seekTo:(progress:Int)->Unit
):SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            //sets the playing file progress to the same seekbar progressive, in relative scale
            seekTo(progress)
            onProgressChange(getCurrentPosition())
            //Also updates the textView because the coroutine only runs every 1 second
         //   progressText.text = SimpleDateFormat("mm:ss").format(mediaPlayer.currentPosition)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}