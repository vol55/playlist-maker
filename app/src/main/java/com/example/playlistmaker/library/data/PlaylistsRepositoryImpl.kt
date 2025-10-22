package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.toDomain
import com.example.playlistmaker.library.data.db.toEntity
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
