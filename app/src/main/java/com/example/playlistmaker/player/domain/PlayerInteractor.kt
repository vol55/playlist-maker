package com.example.playlistmaker.player.domain

interface PlayerInteractor {
    fun prepare(url: String, onReady: () -> Unit, onComplete: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}
