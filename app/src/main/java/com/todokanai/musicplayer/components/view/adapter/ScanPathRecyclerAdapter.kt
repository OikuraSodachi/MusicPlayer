package com.todokanai.musicplayer.components.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.view.holder.ScanPathViewHolder

class ScanPathRecyclerAdapter(private val onClick:(path:String)->Unit):RecyclerView.Adapter<ScanPathViewHolder>() {
    var itemList = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanPathViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scanpath_view_holder, parent, false)
        return ScanPathViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ScanPathViewHolder, position: Int) {
        val item = itemList[position]
        holder.setContents(onClick,item)
    }
}