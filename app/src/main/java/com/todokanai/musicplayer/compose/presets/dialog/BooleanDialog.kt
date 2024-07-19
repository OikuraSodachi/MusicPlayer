package com.todokanai.musicplayer.compose.presets.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BooleanDialog(
    modifier: Modifier,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    message: String,
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

@Preview
@Composable
private fun BooleanDialogPreview(){
    Surface() {
        BooleanDialog(
            modifier = Modifier,
            title = "BooleanDialog",
            message = "Message",
            onConfirm = {  },
            onCancel = { }
        )
    }
}