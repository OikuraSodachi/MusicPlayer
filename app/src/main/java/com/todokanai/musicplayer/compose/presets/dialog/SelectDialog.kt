package com.todokanai.musicplayer.compose.presets.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SelectDialog(
    title:String?,
    items: List<Pair<String,()->Unit>>,
    selectedItem:String,
    onDismissRequest:()->Unit,
    confirmText:String?,
    dismissText:String?,
    onConfirm:(()->Unit)?,
    onCancel:(()->Unit)?,
    modifier : Modifier = Modifier
){
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            title?.let{
                Text(title)
            }
        },
        text = {
            LazyColumn() {
                items(items.size) {
                    val item = items[it]
                    Row(
                        modifier = Modifier
                            .padding(0.dp, 2.dp)
                            .clickable { item.second() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedItem.contains(item.first),
                            onClick = null,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(10.dp)
                        )
                        Text(text = item.first)
                    }
                }
            }
        },
        confirmButton = {
            onConfirm?.let{
                Button(
                    onClick = { onConfirm() }
                ) {
                    confirmText?.let{
                        Text(confirmText)
                    }
                }
            }
        },
        dismissButton = {
            onCancel?.let{
                Button(
                    onClick = { onCancel() }
                ) {
                    dismissText?.let{
                        Text(dismissText)
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun SelectDialogPreview(){
    val items = listOf(Pair("Item1",{}),Pair("Item2",{}))
    SelectDialog(
        title = "Title",
        items = items,
        selectedItem = "Item2",
        onDismissRequest = {},
        dismissText = "cancel",
        confirmText = "Confirm",
        onConfirm = {},
        onCancel = {}
    )
}