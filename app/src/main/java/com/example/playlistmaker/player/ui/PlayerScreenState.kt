package com.example.playlistmaker.player.ui

import com.example.playlistmaker.library.domain.Playlist

data class PlayerScreenState(
    val playerState: PlayerViewModel.PlayerState = PlayerViewModel.PlayerState.DEFAULT,
    val progressTime: String = "00:00",
    val isFavorite: Boolean = false,
    val playlists: List<Playlist> = emptyList()
)
