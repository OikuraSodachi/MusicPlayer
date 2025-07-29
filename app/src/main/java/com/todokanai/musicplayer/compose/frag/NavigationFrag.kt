package com.todokanai.musicplayer.compose.frag

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.myobjects.Constants.FRAG_MUSIC_LIST
import com.todokanai.musicplayer.myobjects.Constants.FRAG_PLAYING
import com.todokanai.musicplayer.myobjects.Constants.FRAG_SETTINGS
import com.todokanai.musicplayer.myobjects.Constants.FRAG_TEST

@Composable
fun NavigationFrag(
    fragCode: MutableState<String>,
    modifier : Modifier = Modifier
){

    /** Enable TestFragment **/
    val shouldShowTestFrag = false

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ){
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = {fragCode.value = FRAG_MUSIC_LIST}
        ){
            Image(painter = painterResource(id = R.drawable.baseline_list_24),null)
        }

        Button(
            modifier = Modifier
                .weight(1f),
            onClick = { fragCode.value = FRAG_PLAYING }
        ){
            Image(painter = painterResource(id = R.drawable.baseline_play_arrow_24), contentDescription = null)
        }

        Button(
            modifier = Modifier
                .weight(1f),
            onClick = {fragCode.value = FRAG_SETTINGS}
        ){
            Image(painterResource(id = R.drawable.baseline_settings_24),null)
        }

        if(shouldShowTestFrag) {
            TextButton(
                modifier = Modifier
                    .weight(1f),
                onClick = { fragCode.value = FRAG_TEST }
            ) {
                Text(stringResource(id = R.string.test_frag))
            }
        }

    }
    println("Recomposition: NavigationFrag")
}