package com.todokanai.musicplayer.compose.presets.dropdownmenu

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun MyDropdownMenu(
    contents:List<Pair<String,()->Unit>>,
    expanded : MutableState<Boolean>
) {
    DropdownMenu(
        modifier = Modifier.wrapContentSize(),
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        contents.forEach {
            DropdownMenuItem(
                text = { Text(it.first) },
                onClick = { it.second() }
            )
        }
    }
}