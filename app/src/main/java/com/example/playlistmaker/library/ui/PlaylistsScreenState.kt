package com.example.playlistmaker.library.ui

import com.example.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsScreenState {
    object Empty : PlaylistsScreenState
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState
}
