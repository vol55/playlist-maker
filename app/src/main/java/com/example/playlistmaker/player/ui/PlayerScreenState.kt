package com.example.playlistmaker.player.ui

import com.example.playlistmaker.library.domain.models.Playlist

data class PlayerScreenState(
    val isFavorite: Boolean = false, val playlists: List<Playlist> = emptyList()
)
