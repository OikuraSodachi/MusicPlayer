package com.todokanai.musicplayer.compose.frag.settingsfrag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.dialog.pathpicker.PathPickerDialog
import com.todokanai.musicplayer.compose.frag.settingsfrag.holder.ScanPathHolder
import com.todokanai.musicplayer.viewmodel.SettingsViewModel
import timber.log.Timber

@Composable
fun SettingsFrag(
    modifier:Modifier,
    viewModel:SettingsViewModel = hiltViewModel()
){
    /*
   // val scanPathList = viewModel.pathList.collectAsStateWithLifecycle()
    var isDialogOn by remember{ mutableStateOf(false) }

    if(isDialogOn){

        PathPickerDialog(
            modifier = Modifier,
            onDismiss = { isDialogOn = false }
        )

    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ){
        Row() {
            Button(
                onClick = { isDialogOn = true },
                shape = CutCornerShape(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.add_path)
                )

               // Image(painter = painterResource(id = R.drawable.ic_baseline_folder_24),null)
            }

            Button(
                onClick = {  },
                shape = CutCornerShape(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.scan)
                )
            }

            Button(
                onClick = {viewModel.clear()},
                shape = CutCornerShape(5.dp),
            ){
                Text(text = stringResource(id = R.string.clear))
            }
        }

        if(scanPathList.value.isEmpty()){

            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                text = stringResource(id = R.string.scanPath_empty)
            )

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Gray)
            ) {
                items(scanPathList.value.size) { index ->
                    val item = scanPathList.value[index]

                    ScanPathHolder(
                        modifier = Modifier,
                        deleteIcon = R.drawable.baseline_delete_24,
                        deleteItem = { viewModel.deleteScanPath(it) },
                        absolutePath = item
                    )
                }
            }
        }
    }
    Timber.d("Recomposition")

     */
}