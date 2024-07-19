package com.todokanai.musicplayer.components.view.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.dataclass.MusicHolderItem

class MusicViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    private val imageAlbum = itemView.findViewById<ImageView>(R.id.imageAlbum)
    private val textArtist = itemView.findViewById<TextView>(R.id.textArtist)
    private val textTitle = itemView.findViewById<TextView>(R.id.textTitle)
    private val textDuration = itemView.findViewById<TextView>(R.id.textDuration)

    fun setMusic(music: MusicHolderItem) {
        Picasso.get().load(music.albumUri).into(imageAlbum)
        textArtist.text = music.artist
        textTitle.text = music.title
        textTitle.isSelected =true
        textDuration.text = music.durationText
    }
}