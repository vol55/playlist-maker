package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.models.PlaylistWithTracks
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist): Int
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrack(track: Track, playlistId: Int)
    suspend fun removeTrack(playlistId: Int, trackId: Int)
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean
    fun saveCover(uri: Any): File?
    fun getPlaylistWithTracks(playlistId: Int): Flow<PlaylistWithTracks>
}
