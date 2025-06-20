package com.example.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.widget.TextView
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    private lateinit var play: ImageButton
    private lateinit var track: Track
    private lateinit var handler: Handler
    private lateinit var tvCurrentTime: TextView
    private lateinit var updateTimerTask: Runnable

    private val duration = 30
    private var timer = 0

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        tvCurrentTime = findViewById(R.id.tvCurrentTime)

        handler = Handler(Looper.getMainLooper())

        track = intent.getParcelableExtra(TRACK, Track::class.java) ?: run {
            finish()
            return
        }

        play = findViewById(R.id.ibPlayButton)
        play.setOnClickListener { playbackControl() }
        preparePlayer()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarButtonBack)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        val releaseDate = track.releaseDate
        val year = releaseDate.length.let { if (it >= 4) releaseDate.substring(0, 4) else "" }

        findViewById<TextView>(R.id.tvTrackTitle).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvTrackDuration).text = duration
        findViewById<TextView>(R.id.tvCollectionName).text = track.collectionName
        findViewById<TextView>(R.id.tvReleaseDate).text = year
        findViewById<TextView>(R.id.tvPrimaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountry).text = track.country


        val artworkImageView = findViewById<ImageView>(R.id.ivCoverArtwork)
        Glide.with(this)
            .load(track.getCoverArtwork())
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            setPlayIcon()
            playerState = STATE_PREPARED
            handler.removeCallbacks(updateTimerTask)
            timer = 0
            tvCurrentTime.text = getString(R.string.current_time_start)
        }
    }

    private fun startPlayer() {
        setPauseIcon()
        mediaPlayer.start()
        playerState = STATE_PLAYING
        updateTimerTask = createUpdateTimerTask()
        handler.post(updateTimerTask)
    }

    private fun pausePlayer() {
        setPlayIcon()
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerTask)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (timer < duration) {
                    tvCurrentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer.currentPosition)
                    timer += 1
                    handler.postDelayed(this, DELAY)
                }
            }
        }
    }

    private fun setPlayIcon() {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.play, value, true)
        play.setImageResource(value.resourceId)
    }

    private fun setPauseIcon() {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.pause, value, true)
        play.setImageResource(value.resourceId)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L

        const val TRACK = "track"
    }
}
