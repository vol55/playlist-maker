package com.example.playlistmaker.player.domain

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    private var onReadyCallback: (() -> Unit)? = null
    private var onCompleteCallback: (() -> Unit)? = null

    override fun prepare(url: String, onReady: () -> Unit, onComplete: () -> Unit) {
        onReadyCallback = onReady
        onCompleteCallback = onComplete
        repository.prepare(url, onReady, onComplete)
    }

    override fun play() = repository.start()
    override fun pause() = repository.pause()
    override fun release() = repository.release()
    override fun isPlaying(): Boolean = repository.isPlaying()
    override fun getCurrentPosition(): Int = repository.getCurrentPosition()
}
