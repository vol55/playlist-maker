package com.example.playlistmaker.player.ui

data class PlayerScreenState(
    val playerState: PlayerViewModel.PlayerState = PlayerViewModel.PlayerState.DEFAULT,
    val progressTime: String = "00:00"
)
