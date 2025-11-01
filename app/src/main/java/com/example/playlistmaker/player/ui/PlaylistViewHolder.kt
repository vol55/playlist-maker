package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.utils.dpToPx

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.cover)
    private val title: TextView = itemView.findViewById(R.id.title)
    private val count: TextView = itemView.findViewById(R.id.count)

    fun bind(playlist: Playlist) {
        title.text = playlist.title
        val resources = itemView.resources
        val trackCountText = resources.getQuantityString(
            R.plurals.track_count, playlist.trackCount, playlist.trackCount
        )
        count.text = trackCountText

        playlist.coverImagePath?.let { coverPath ->
            Glide.with(itemView.context).load(coverPath).apply(
                RequestOptions().placeholder(R.drawable.placeholder)
                    .transform(RoundedCorners(itemView.context.dpToPx(8f)))
            ).into(cover)
        } ?: run {
            cover.setImageResource(R.drawable.placeholder)
        }
    }
}
