package com.todokanai.musicplayer.compose.dialog.pathpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.dialog.pathpicker.listview.DirectoryListView
import com.todokanai.musicplayer.compose.dialog.pathpicker.listview.FileListView
import com.todokanai.musicplayer.compose.dialog.pathpicker.listview.StorageListView
import com.todokanai.musicplayer.viewmodel.PathPickerViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathPickerDialog(
    modifier: Modifier,
    onDismiss:()->Unit,
    viewModel: PathPickerViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val currentPath = viewModel.currentPath.collectAsStateWithLifecycle()
    val dirTree = viewModel.dirTree.collectAsStateWithLifecycle()
    val itemList = viewModel.fileHolderItemList.collectAsStateWithLifecycle()
    val isStorageSelectView = viewModel.isStorageSelectView.collectAsStateWithLifecycle()

   // val fModule = FileModule(Variables.defaultStorage)

    AlertDialog(
        modifier = modifier
            .background(Color.Gray),
        onDismissRequest = {},
        content = {
            Column() {
                if(isStorageSelectView.value){
                    StorageListView(
                        modifier = Modifier,
                        itemList = viewModel.storageList(context),
                        onClick = {viewModel.onSelectStorage(it,context)}
                    )
                }else {
                    DirectoryListView(
                        dirTree = dirTree.value,
                        updateCurrentPath = { viewModel.updateCurrentPath(it, context) }
                    )

                    FileListView(
                        modifier = Modifier
                            .weight(1f),
                        itemList = itemList.value,
                        onClick = { viewModel.updateCurrentPath(it, context) },
                        toParent = {
                            viewModel.toParent(
                                currentPath.value,
                                context
                            )
                        }
                    )


                }
                Row {
                    Button(
                        onClick = { onDismiss() }
                    ) {
                        Text(text = stringResource(id = R.string.path_picker_close))
                    }
                    Button(
                        onClick = { viewModel.insertScanPath(currentPath.value) }
                    ) {
                        Text(text = stringResource(id = R.string.add_this_directory))
                    }
                }
            }
        }
    )
    Timber.d("Recomposition")
}

@Preview
@Composable
private fun PathPickerDialogPreview(){
    PathPickerDialog(modifier = Modifier, onDismiss = { })
}