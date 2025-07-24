package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var tvCurrentTime: TextView
    private lateinit var track: Track
    private lateinit var playerViewModel: PlayerViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val trackJson = intent.getStringExtra("TRACK_JSON_KEY")
        if (trackJson == null) {
            finish()
            return
        }

        track = Gson().fromJson(trackJson, Track::class.java)

        playButton = findViewById(R.id.ibPlayButton)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)

        playerViewModel = ViewModelProvider(
            this, PlayerViewModel.getFactory(track.previewUrl)
        )[PlayerViewModel::class.java]

        playerViewModel.screenState.observe(this) { state ->
            when (state.playerState) {
                PlayerViewModel.PlayerState.DEFAULT -> {
                    playButton.isEnabled = false
                    setPlayIcon()
                }

                PlayerViewModel.PlayerState.PREPARED -> {
                    playButton.isEnabled = true
                    setPlayIcon()
                }

                PlayerViewModel.PlayerState.PLAYING -> {
                    playButton.isEnabled = true
                    setPauseIcon()
                }

                PlayerViewModel.PlayerState.PAUSED -> {
                    playButton.isEnabled = true
                    setPlayIcon()
                }
            }
            tvCurrentTime.text = state.progressTime
        }

        playButton.setOnClickListener { playerViewModel.onPlayButtonClicked() }

        findViewById<MaterialToolbar>(R.id.toolbarButtonBack).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        initTrackInfo(track)
    }

    private fun initTrackInfo(track: Track) {
        val duration = track.trackDuration
        val releaseYear = track.trackReleaseYear

        findViewById<TextView>(R.id.tvTrackTitle).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvTrackDuration).text = duration
        findViewById<TextView>(R.id.tvCollectionName).text = track.collectionName
        findViewById<TextView>(R.id.tvReleaseDate).text = releaseYear
        findViewById<TextView>(R.id.tvPrimaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountry).text = track.country

        Glide.with(this).load(track.coverArtworkUrl).apply(
            RequestOptions().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(this.dpToPx(8f)))
        ).into(findViewById(R.id.ivCoverArtwork))
    }

    private fun setPlayIcon() {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.play, value, true)
        playButton.setImageResource(value.resourceId)
    }

    private fun setPauseIcon() {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.pause, value, true)
        playButton.setImageResource(value.resourceId)
    }

    override fun onPause() {
        super.onPause()
        if (playerViewModel.screenState.value?.playerState == PlayerViewModel.PlayerState.PLAYING) {
            playerViewModel.onPlayButtonClicked()
        }
    }
}
