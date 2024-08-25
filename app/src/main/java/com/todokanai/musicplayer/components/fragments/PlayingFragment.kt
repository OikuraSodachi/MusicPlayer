package com.todokanai.musicplayer.components.fragments

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.todokanai.musicplayer.components.view.SeekBarListener
import com.todokanai.musicplayer.databinding.FragmentPlayingBinding
import com.todokanai.musicplayer.tools.IconPicker
import com.todokanai.musicplayer.viewmodel.PlayingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayingFragment : Fragment() {

    private val binding by lazy{FragmentPlayingBinding.inflate(layoutInflater)}
    private val viewModel:PlayingViewModel by viewModels()
    private val icons = IconPicker()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val seekBarListener = SeekBarListener(
            getCurrentPosition = {viewModel.currentPosition()},
            onProgressChange = {
                binding.songCurrentProgress.text = viewModel.currentPosition().format_td()
            },
            seekTo = {viewModel.seekTo(it)}
        )

        fun seekBarSet(mediaPlayer: MediaPlayer, seekBar: SeekBar) {

            /** Todo: 이거 아마 livedata로 대체해야 할지도? **/
            lifecycleScope.launch {
                while (mediaPlayer.isPlaying) {
                    binding.seekBar.progress = viewModel.currentPosition()
                    binding.songCurrentProgress.text = binding.seekBar.progress.format_td()
                    delay(1000)
                }
            }
            seekBar.setOnSeekBarChangeListener(seekBarListener)
        }

        binding.run{
            repeatButton.setOnClickListener { viewModel.repeat(requireActivity()) }
            previousButton.setOnClickListener { viewModel.prev(requireActivity()) }
            playPauseButton.setOnClickListener { viewModel.pausePlay(requireActivity()) }
            nextButton.setOnClickListener { viewModel.next(requireActivity()) }
            shuffleButton.setOnClickListener { viewModel.shuffle(requireActivity()) }
        }

        viewModel.run{
            currentMusicHolder.asLiveData().observe(viewLifecycleOwner){
                it?.let {
                    binding.run {
                        seekBar.max = viewModel.mediaPlayer.duration
                        playerImage.setImageURI(it.getAlbumUri())
                        artist.text = it.artist
                        songTotalTime.text = it.duration.format_td()
                        title.text = it.title
                    }
                }
            }
            isPlayingHolder.asLiveData().observe(viewLifecycleOwner){
                binding.playPauseButton.setImageResource(icons.pausePlayIcon(it))
                seekBarSet(viewModel.mediaPlayer,binding.seekBar)
            }

            isRepeatingHolder.asLiveData().observe(viewLifecycleOwner){
                binding.repeatButton.setImageResource(icons.repeatIcon(it))
            }
            isShuffledHolder.asLiveData().observe(viewLifecycleOwner){
                binding.shuffleButton.setImageResource(icons.shuffleIcon(it))
            }
        }
        return binding.root
    }

    private fun Int.format_td() = SimpleDateFormat("mm:ss").format(this)
}