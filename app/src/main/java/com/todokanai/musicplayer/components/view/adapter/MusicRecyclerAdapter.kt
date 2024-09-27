package com.todokanai.musicplayer.components.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.base.BaseRecyclerAdapter
import com.todokanai.musicplayer.components.view.holder.MusicViewHolder
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem
import kotlinx.coroutines.flow.Flow

class MusicRecyclerAdapter(
    private val onItemClick:(music:MusicHolderItem)->Unit,
    itemFlow: Flow<List<MusicHolderItem>>,
    lifecycleOwner: LifecycleOwner
): BaseRecyclerAdapter<MusicHolderItem,MusicViewHolder>(itemFlow,lifecycleOwner) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_view_holder, parent, false)
        return MusicViewHolder(view)
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