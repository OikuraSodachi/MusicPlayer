package com.todokanai.musicplayer.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding.dialogLayout.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val dialog = viewModel.directoryDialog.collectAsStateWithLifecycle()
                if(dialog.value){
                    PathPickerDialog(
                        modifier = Modifier,
                        onDismiss = { viewModel.directoryDialogOff() }
                    )
                }
            }
        }

        val adapter = ScanPathRecyclerAdapter({viewModel.deleteScanPath(it)})
        binding.run{
            scanPathRecyclerView.adapter = adapter
            scanPathRecyclerView.layoutManager = LinearLayoutManager(context)
            directoryDialogBtn.setOnClickListener{
                viewModel.directoryDialog()
            }
            applyBtn.setOnClickListener {
                viewModel.apply(requireContext(),applyBtn)
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