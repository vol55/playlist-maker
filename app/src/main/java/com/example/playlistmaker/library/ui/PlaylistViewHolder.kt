package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val playlistImage: ImageView = view.findViewById(R.id.playlistImage)
    private val playlistTitle: TextView = view.findViewById(R.id.playlistTitle)
    private val trackCount: TextView = view.findViewById(R.id.trackCount)

    fun bind(playlist: Playlist) {
        playlistTitle.text = playlist.title
        trackCount.text = "${playlist.trackCount} треков"

        if (!playlist.coverImagePath.isNullOrEmpty()) {
            playlistImage.setImageURI(playlist.coverImagePath.toUri())
        } else {
            playlistImage.setImageResource(R.drawable.placeholder)
        }
    }
}
