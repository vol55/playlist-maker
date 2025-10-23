package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.PlaylistDao
import com.example.playlistmaker.library.data.db.PlaylistTracksDao
import com.example.playlistmaker.library.data.db.mappers.toDomain
import com.example.playlistmaker.library.data.db.mappers.toEntity
import com.example.playlistmaker.library.data.db.mappers.toPlaylistTrackEntity
import com.example.playlistmaker.library.domain.api.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTracksDao: PlaylistTracksDao
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist): Int {
        val id = playlistDao.insertPlaylist(playlist.toEntity())
        return id.toInt()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTrack(track: Track, playlistId: Int) {
        playlistTracksDao.insertTrack(track.toPlaylistTrackEntity(playlistId))
        val count = playlistTracksDao.getTrackCountForPlaylist(playlistId)
        playlistDao.updateTrackCount(playlistId, count)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return playlistTracksDao.isTrackInPlaylist(playlistId, trackId)
    }
}