package com.example.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track: Track? = intent.getParcelableExtra("track", Track::class.java)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarButtonBack)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        val releaseDate = track?.releaseDate
        val year = releaseDate?.length?.let { if (it >= 4) releaseDate.substring(0, 4) else "" }

        findViewById<TextView>(R.id.tvTrackTitle).text = track?.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track?.artistName
        findViewById<TextView>(R.id.tvTrackDuration).text = duration
        findViewById<TextView>(R.id.tvCollectionName).text = track?.collectionName
        findViewById<TextView>(R.id.tvReleaseDate).text = year
        findViewById<TextView>(R.id.tvPrimaryGenreName).text = track?.primaryGenreName
        findViewById<TextView>(R.id.tvCountry).text = track?.country


        val artworkImageView = findViewById<ImageView>(R.id.ivCoverArtwork)
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .transform(RoundedCorners(dpToPx(8f, this)))
            )
            .into(artworkImageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
