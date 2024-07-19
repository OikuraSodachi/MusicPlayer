package com.todokanai.musicplayer.components.view.holder

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.musicplayer.R

class ScanPathViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    fun setContents(onClick:(path:String) -> Unit,path:String){
        itemView.findViewById<TextView>(R.id.scanPathTextView).text = path
        itemView.findViewById<ImageButton>(R.id.deleteBtn).setOnClickListener{
            onClick(path)
        }
    }
}