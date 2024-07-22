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
import com.todokanai.musicplayer.tools.independent.getPhysicalStorages_td
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    val currentPath = fModule.currentPath
    private val converter = DataConverter()

    val isStorageSelectView = MutableStateFlow<Boolean>(true)

    fun storageList(context: Context) = getPhysicalStorages_td(context).map{
        converter.toFileHolderItem(it)
    }

    val fileHolderItemList = fModule.files.map { file ->
        DataConverter().fileHolderItemList(file,Constants.BY_DEFAULT)
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


    fun toParent(file: File?,context: Context) {
        if(getPhysicalStorages_td(context).contains(file)){
            isStorageSelectView.value = true
        } else {
            updateCurrentPath(file?.parentFile, context)
        }
    }

    fun updateCurrentPath(file: File?,context: Context){
       // viewModelScope.launch {
            if(file?.listFiles()!=null){
                fModule.updateCurrentPath(file)
            } else{
                Toast.makeText(context, context.getString(R.string.not_accessible),Toast.LENGTH_SHORT).show()
            }

            file?.listFiles()?.let {
                fModule.updateCurrentPath(file)
            }
      //  }
    }

    fun onSelectStorage(storage:File,context: Context){
        viewModelScope.launch {
            updateCurrentPath(storage, context)
            isStorageSelectView.value = false
        }
    }

    fun insertScanPath(file: File){
        viewModelScope.launch {
            spRepository.insert(file)
        }
    }
}