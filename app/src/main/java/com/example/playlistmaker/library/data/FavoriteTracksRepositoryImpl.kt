package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.TrackDao
import com.example.playlistmaker.library.data.db.mappers.toDomain
import com.example.playlistmaker.library.data.db.mappers.toEntity
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FavoriteTracksRepositoryImpl(
    private val trackDao: TrackDao
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        trackDao.insertTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        trackDao.deleteTrack(track.trackId)
    }

    override fun getTracks(): Flow<List<Track>> {
        return trackDao.getTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return trackDao.isFavorite(trackId)
    }
}
