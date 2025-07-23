package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(
    private val player: () -> MediaPlayer
) : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun prepare(
        url: String, onPrepared: () -> Unit, onComplete: () -> Unit
    ) {
        release()
        mediaPlayer = player().apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onComplete() }
            prepareAsync()
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}