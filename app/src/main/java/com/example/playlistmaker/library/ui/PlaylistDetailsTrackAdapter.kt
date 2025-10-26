package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track


class PlaylistDetailsTrackAdapter(
    private val tracks: List<Track>, private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return PlaylistDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistDetailsViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onItemClick(track) }
    }

    override fun getItemCount(): Int = tracks.size
}
