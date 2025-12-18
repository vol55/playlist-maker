package com.example.playlistmaker.player.ui

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.service.PlayerState

data class PlayerScreenState(
    val isFavorite: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val playerState: PlayerState = PlayerState.Default()
)
