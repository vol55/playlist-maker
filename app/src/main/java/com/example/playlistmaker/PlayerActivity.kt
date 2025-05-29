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

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarButtonBack)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val trackName = intent.getStringExtra("trackName").orEmpty()
        val artistName = intent.getStringExtra("artistName").orEmpty()
        val duration =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(intent.getIntExtra("duration", 0))
        val collectionName = intent.getStringExtra("collectionName").orEmpty()
        val releaseDate = intent.getStringExtra("releaseDate").orEmpty()
        val year = if (releaseDate.length >= 4) releaseDate.substring(0, 4) else ""
        val primaryGenreName = intent.getStringExtra("primaryGenreName").orEmpty()
        val country = intent.getStringExtra("country").orEmpty()
        val artworkUrl = intent.getStringExtra("artwork").orEmpty()

        findViewById<TextView>(R.id.tvTrackTitle).text = trackName
        findViewById<TextView>(R.id.tvArtistName).text = artistName
        findViewById<TextView>(R.id.tvTrackDuration).text = duration
        findViewById<TextView>(R.id.tvCollectionName).text = collectionName
        findViewById<TextView>(R.id.tvReleaseDate).text = year
        findViewById<TextView>(R.id.tvPrimaryGenreName).text = primaryGenreName
        findViewById<TextView>(R.id.tvCountry).text = country


        val artworkImageView = findViewById<ImageView>(R.id.ivCoverArtwork)
        Glide.with(this)
            .load(artworkUrl)
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
