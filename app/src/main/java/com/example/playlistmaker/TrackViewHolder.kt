package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)

    fun bind(item: Track) {
        val description = "${item.artistName}  •  ${item.trackTime}"
        tvTrackName.text = item.trackName
        tvDescription.text = description

        Glide.with(itemView.context)
            .load(item.artworkUrl100)
            .fitCenter()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(dpToPx(2f, itemView.context))))
            .placeholder(R.drawable.image)
            .into(ivArtwork)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
