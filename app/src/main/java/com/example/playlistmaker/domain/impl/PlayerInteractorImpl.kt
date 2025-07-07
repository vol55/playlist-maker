package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl : PlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null

    private var onReadyCallback: (() -> Unit)? = null
    private var onCompleteCallback: (() -> Unit)? = null

    override fun prepare(url: String, onReady: () -> Unit, onComplete: () -> Unit) {
        release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                onReady()
            }
            setOnCompletionListener {
                onComplete()
            }
            prepareAsync()
        }

        onReadyCallback = onReady
        onCompleteCallback = onComplete
    }

    override fun play() {
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
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}
