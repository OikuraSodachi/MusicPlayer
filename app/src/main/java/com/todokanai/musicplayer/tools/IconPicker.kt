package com.todokanai.musicplayer.tools

import com.todokanai.musicplayer.R

class IconPicker() {


    val nextIcon = R.drawable.baseline_skip_next_24

    val prevIcon = R.drawable.baseline_skip_previous_24

    private val playIcon = R.drawable.baseline_play_arrow_24

    private val pauseIcon = R.drawable.baseline_pause_24

    private val shuffleIcon = R.drawable.baseline_shuffle_24

    private val nonShuffleIcon = R.drawable.baseline_arrow_right_alt_24

    private val repeatIcon = R.drawable.baseline_repeat_one_24

    private val nonRepeatIcon = R.drawable.baseline_repeat_24

    fun pausePlayIcon(isPlaying:Boolean):Int{
        if(isPlaying){
            return pauseIcon
        }else{
            return playIcon
        }
    }

    fun shuffleIcon(isShuffled:Boolean):Int{
        if(isShuffled){
            return shuffleIcon
        } else{
            return nonShuffleIcon
        }
    }

    fun repeatIcon(isLooping:Boolean):Int{
        if(isLooping){
            return repeatIcon
        } else{
            return nonRepeatIcon
        }
    }

}