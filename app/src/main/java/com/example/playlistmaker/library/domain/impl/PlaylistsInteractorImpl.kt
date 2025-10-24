package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.data.db.PlaylistWithTracks
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.api.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File


class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Int {
        return repository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrack(track: Track, playlistId: Int) {
        repository.addTrack(track, playlistId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return repository.isTrackInPlaylist(playlistId, trackId)
    }

    override fun saveCover(uri: Any): File? {
        return repository.saveCover(uri)
    }

    override fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>> {
        return repository.getPlaylistsWithTracks()
    }
}
