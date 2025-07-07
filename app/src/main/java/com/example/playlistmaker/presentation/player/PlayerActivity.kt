package com.example.playlistmaker.presentation.player

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var tvCurrentTime: TextView
    private lateinit var handler: Handler
    private lateinit var updateTimerTask: Runnable
    private lateinit var track: Track
    private lateinit var playerInteractor: PlayerInteractor

    private var playerState = STATE_DEFAULT

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerInteractor = Creator.providePlayerInteractor()
        handler = Handler(Looper.getMainLooper())

        track = intent.getParcelableExtra(TRACK, Track::class.java) ?: run {
            finish()
            return
        }

        playButton = findViewById(R.id.ibPlayButton)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)

        playButton.setOnClickListener { playbackControl() }

        preparePlayer()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarButtonBack)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        initTrackInfo(track)
    }

    private fun initTrackInfo(track: Track) {
        val duration = track.getDuration()
        val releaseYear = track.getReleaseYear()

        findViewById<TextView>(R.id.tvTrackTitle).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvTrackDuration).text = duration
        findViewById<TextView>(R.id.tvCollectionName).text = track.collectionName
        findViewById<TextView>(R.id.tvReleaseDate).text = releaseYear
        findViewById<TextView>(R.id.tvPrimaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountry).text = track.country

        Glide.with(this).load(track.getCoverArtwork()).apply(
            RequestOptions().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(8f, this)))
        ).into(findViewById(R.id.ivCoverArtwork))
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    private fun preparePlayer() {
        playerInteractor.prepare(track.previewUrl, ::onPrepared, ::onCompletion)
    }

    private fun onPrepared() {
        playButton.isEnabled = true
        playerState = STATE_PREPARED
    }

    private fun onCompletion() {
        setPlayIcon()
        playerState = STATE_PREPARED
        handler.removeCallbacks(updateTimerTask)
        tvCurrentTime.text = getString(R.string.current_time_start)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        playerState = STATE_PLAYING
        setPauseIcon()

        updateTimerTask = createUpdateTimerTask()
        handler.post(updateTimerTask)
    }

    private fun pausePlayer() {
        playerInteractor.pause()
        playerState = STATE_PAUSED
        setPlayIcon()
        handler.removeCallbacks(updateTimerTask)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition = playerInteractor.getCurrentPosition()
                if (currentPosition < PREVIEW_DURATION) {
                    tvCurrentTime.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    handler.postDelayed(this, DELAY)
                }
            }
        }
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

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val PREVIEW_DURATION = 30_000
        private const val DELAY = 500L

        const val TRACK = "track"
    }
}
