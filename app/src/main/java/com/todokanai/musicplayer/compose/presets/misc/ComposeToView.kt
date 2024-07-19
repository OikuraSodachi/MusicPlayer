package com.todokanai.musicplayer.compose.presets.misc

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ComposeToView(
    view:(viewContext:Context)->View
) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            // Creates view

            /** = OnCreate (...?) **/
            view(context).apply {
                // Sets up listeners for View -> Compose communication
                /*
                setOnClickListener {
                    selectedItem = 1
                }
                 */
            }
        },
        update = { view ->
            /*
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
            view.selectedItem = selectedItem

             */
        }
    )
}
