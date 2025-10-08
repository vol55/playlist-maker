package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.toDomain
import com.example.playlistmaker.library.data.db.toEntity
import com.example.playlistmaker.library.domain.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(track.trackId)
    }

    override fun getTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
