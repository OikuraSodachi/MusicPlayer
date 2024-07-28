package com.todokanai.musicplayer.compose.presets.textinput

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


/**  Height 관련 살짝 불안정(?) **/
@Composable
fun TextInput(
    defaultText:String,
    onConfirm:(String)->Unit,
    confirmButton: @Composable (modifier:Modifier) -> Unit,
    modifier : Modifier = Modifier
){
    var text by remember { mutableStateOf("") }

    Row (
        modifier = modifier
    ){
        TextField(
            value = text,
            placeholder = { Text(defaultText) },
            onValueChange = {text = it},
            singleLine = true
        )
        confirmButton(
            Modifier
                .clickable{onConfirm(text)}
        )
    }
}

/**  Height 관련 살짝 불안정(?) **/
@Composable
fun TextInputPreset(
    defaultText:String,
    inputButtonText:String,
    onConfirm:(String)->Unit,
    modifier : Modifier = Modifier
){
    var text by remember { mutableStateOf("") }

    Row (
        modifier = modifier
            .wrapContentSize()
    ){
        TextField(
            modifier = Modifier
                .weight(1f),
            value = text,
            placeholder = { Text(defaultText) },
            onValueChange = {text = it},
            singleLine = true
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = { onConfirm(text) }
        ) {
            Text(
                text = inputButtonText,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun TextInputPresetPreview(){
    Surface {
        TextInputPreset(
            defaultText = "defaultText",
            inputButtonText = "Insert",
            onConfirm = {}
        )
    }
}