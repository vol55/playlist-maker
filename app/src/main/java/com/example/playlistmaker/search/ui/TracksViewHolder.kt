package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)

    fun bind(item: Track) {
        val description = "${item.artistName}  •  ${item.trackDuration}"
        tvTrackName.text = item.trackName
        tvDescription.text = description

        Glide.with(itemView.context).load(item.coverArtworkUrl).fitCenter()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(itemView.context.dpToPx(2f))))
            .placeholder(R.drawable.placeholder).into(ivArtwork)
    }
}
