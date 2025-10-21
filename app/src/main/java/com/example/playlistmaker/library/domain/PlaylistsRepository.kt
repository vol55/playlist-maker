package com.example.playlistmaker.library.domain

import com.example.playlistmaker.player.domain.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist)
}
