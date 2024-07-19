package com.todokanai.musicplayer.compose.presets.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EditTextDialog(
    modifier: Modifier,
    title: String,
    defaultText:String,
    cancelText:String = "Cancel",
    confirmText:String = "Confirm",
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        title = { Text(text = title) },
        text = {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                placeholder = {Text(defaultText)},
                onValueChange = { text = it },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(text)
                    onCancel()
                }
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCancel()
                }
            ) {
                Text(text = cancelText)
            }
        }
    )
}

@Preview
@Composable
private fun EditTextDialogPreview(){
    EditTextDialog(
        modifier = Modifier,
        title = "title",
        defaultText = "default text",
        onConfirm = {},
        onCancel = {}
    )
}