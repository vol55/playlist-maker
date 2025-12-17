package com.example.playlistmaker.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.TrackUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService : Service(), MusicServiceInterface {

    private val binder = MusicServiceBinder()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    override val playerState = _playerState.asStateFlow()

    private var track: TrackUi? = null

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID, "Music service", NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun foregroundServiceType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
    } else {
        0
    }

    override fun onBind(intent: Intent?): IBinder {
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(ARG_TRACK, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getParcelableExtra(ARG_TRACK)
        }
        initMediaPlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    private fun initMediaPlayer() {
        val previewUrl = track?.previewUrl ?: return

        try {
            mediaPlayer?.setDataSource(previewUrl)
            mediaPlayer?.prepareAsync()

        } catch (_: Exception) {
            releasePlayer()
            _playerState.value = PlayerState.Default()
            return
        }

        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared()
        }

        mediaPlayer?.setOnCompletionListener {
            _playerState.value = PlayerState.Prepared()
            resetPlayer()
        }
    }

    override fun startPlayer() {
        val startPosition = when (_playerState.value) {
            is PlayerState.Paused -> getCurrentPlayerPosition()
            else -> "00:00"
        }

        _playerState.value = PlayerState.Playing(startPosition)
        mediaPlayer?.start()
        startTimer()
    }

    override fun showNotification() {
        val currentTrack = track ?: return

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Playlist Maker")
                .setContentText("${currentTrack.artistName} - ${currentTrack.trackName}")
                .setSmallIcon(R.drawable.library).setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE).setOnlyAlertOnce(true).build()

        ServiceCompat.startForeground(
            this, NOTIFICATION_ID, notification, foregroundServiceType()
        )
    }


    override fun hideNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        _playerState.value = PlayerState.Default()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null

    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }

    private fun resetPlayer() {
        timerJob?.cancel()
        _playerState.value = PlayerState.Prepared("00:00")
        hideNotification()
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val ARG_TRACK = "track"
        private const val DELAY = 200L
        private const val CHANNEL_ID = "music_service_channel"
        private const val NOTIFICATION_ID = 100
    }
}
