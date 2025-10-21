package com.example.playlistmaker.library.domain

import com.example.playlistmaker.player.domain.Playlist


interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist)
}
