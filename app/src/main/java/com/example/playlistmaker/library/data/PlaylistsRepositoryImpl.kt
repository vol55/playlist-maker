package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.mappers.toDomain
import com.example.playlistmaker.library.data.db.mappers.toEntity
import com.example.playlistmaker.library.data.db.mappers.toPlaylistTrackEntity
import com.example.playlistmaker.library.domain.api.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist): Int {
        val id = appDatabase.playlistDao().insertPlaylist(playlist.toEntity())
        return id.toInt()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTrack(track: Track, playlistId: Int) {
        val playlistTracksDao = appDatabase.playlistTracksDao()

        playlistTracksDao.insertTrack(track.toPlaylistTrackEntity(playlistId))
        val count = playlistTracksDao.getTrackCountForPlaylist(playlistId)
        appDatabase.playlistDao().updateTrackCount(playlistId, count)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return appDatabase.playlistTracksDao().isTrackInPlaylist(playlistId, trackId)
    }
}
