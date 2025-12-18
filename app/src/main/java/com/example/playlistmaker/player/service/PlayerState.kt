package com.example.playlistmaker.player.service

sealed class PlayerState(
    val buttonState: Boolean = false, val progress: String = "00:00"
) {
    class Default : PlayerState()
    class Prepared(progress: String = "00:00") : PlayerState(true, progress)
    class Playing(progress: String) : PlayerState(true, progress)
    class Paused(progress: String) : PlayerState(true, progress)
}
