package com.todokanai.musicplayer.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.todokanai.musicplayer.components.view.adapter.ScanPathRecyclerAdapter
import com.todokanai.musicplayer.compose.dialog.pathpicker.PathPickerDialog
import com.todokanai.musicplayer.databinding.FragmentSettingsBinding
import com.todokanai.musicplayer.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val binding by lazy{FragmentSettingsBinding.inflate(layoutInflater)}
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var dialogTest: MutableState<Boolean>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val adapter = ScanPathRecyclerAdapter { viewModel.deleteScanPath(it) }

        binding.run {
            dialogLayout.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    dialogTest = remember { mutableStateOf(false) }
                    if (dialogTest.value) {
                        PathPickerDialog(
                            onDismiss = { dialogTest.value = false }
                        )
                    }
                }
            }
            scanPathRecyclerView.run{
                this.adapter = adapter
                this.layoutManager = LinearLayoutManager(context)
            }
            directoryDialogBtn.setOnClickListener {
                dialogTest.value = true
            }
            applyBtn.setOnClickListener {
                viewModel.apply(requireContext(), applyBtn)
            }
        }

        viewModel.run{
            pathList.asLiveData().observe(viewLifecycleOwner){
                adapter.itemList = it
                adapter.notifyDataSetChanged()
            }
        }
        return binding.root
    }
}