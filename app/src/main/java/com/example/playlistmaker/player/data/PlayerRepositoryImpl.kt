package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(
    private val player: MediaPlayer
) : PlayerRepository {

    override fun prepare(
        url: String, onPrepared: () -> Unit, onComplete: () -> Unit
    ) {
        player.reset()
        player.setDataSource(url)
        player.setOnPreparedListener { onPrepared() }
        player.setOnCompletionListener { onComplete() }
        player.prepareAsync()
    }

    override fun start() {
        player.start()
    }

    override fun pause() {
        player.pause()
    }

    override fun release() {
        player.release()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }
}
