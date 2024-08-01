package com.todokanai.musicplayer.components.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.view.holder.MusicViewHolder
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem

class MusicRecyclerAdapter(private val onItemClick:(music:MusicHolderItem)->Unit):RecyclerView.Adapter<MusicViewHolder>() {

    var itemList = emptyList<MusicHolderItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_view_holder, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            setMusic(item)
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}