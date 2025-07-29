package com.todokanai.musicplayer.compose.presets.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BooleanDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onCancel() },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onCancel()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCancel()

                }
            ) {
                Text(text = "Cancel")
            }
        }
    )

}