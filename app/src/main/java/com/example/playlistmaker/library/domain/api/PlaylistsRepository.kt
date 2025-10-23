package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist): Int
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrack(track: Track, playlistId: Int)
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean
}
