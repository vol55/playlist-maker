package com.example.playlistmaker.library.domain

import kotlinx.coroutines.flow.Flow


interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}
