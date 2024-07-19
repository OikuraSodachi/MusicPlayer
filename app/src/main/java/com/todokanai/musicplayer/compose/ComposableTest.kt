package com.todokanai.musicplayer.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SeekBarExample(
    onValueChange:(Float)->Unit
) {
    var progress by remember { mutableStateOf(0.5f) } // 초기값 설정


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progress: ${progress * 100}%",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = progress,
            onValueChange = { newValue ->
                //progress = newValue
                onValueChange(newValue)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }


}

@Preview
@Composable
private fun SeekBarExamplePreview(){
    Surface {
        SeekBarExample(
            onValueChange = {}
        )

    }
}