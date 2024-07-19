package com.todokanai.musicplayer.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.DataConverter
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.repository.ScanPathRepository
import com.todokanai.musicplayer.tools.independent.FileModule
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PathPickerViewModel @Inject constructor(
    private val spRepository: ScanPathRepository,
) : ViewModel() {


    private val fModule = FileModule(Variables.defaultStorage)
    private val converter = DataConverter()

    val currentPath = fModule.currentPath

    val fileHolderItemList = fModule.files.map { file ->
        converter.fileHolderItemList(file,Constants.BY_DEFAULT)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5),
        initialValue = emptyList()
    )

    val dirTree = fModule.dirTree.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5),
        initialValue = emptyList()
    )

    fun updateCurrentPath(file: File?,context: Context){
        viewModelScope.launch {
            if(file?.listFiles()!=null){
                fModule.updateCurrentPath(file)
            } else{
                Toast.makeText(context, context.getString(R.string.not_accessible),Toast.LENGTH_SHORT).show()
            }

            file?.listFiles()?.let {
                fModule.updateCurrentPath(file)
            }
        }
    }

    fun insertScanPath(file: File){
        viewModelScope.launch {
            spRepository.insert(file)
        }
    }
}