package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Result<List<Track>>> {
        return repository.searchTracks(expression).map { response ->
            if (response is TracksSearchResponse) {
                if (response.resultCode == -1) {
                    Result.failure(Exception())
                } else {
                    val tracks = response.results.map { dto: TrackDto ->
                        Track(
                            trackId = dto.trackId ?: 0,
                            trackName = dto.trackName.orEmpty(),
                            artistName = dto.artistName.orEmpty(),
                            trackTimeMillis = dto.trackTimeMillis ?: 0,
                            trackDuration = dto.getDuration(),
                            artworkUrl100 = dto.artworkUrl100.orEmpty(),
                            coverArtworkUrl = dto.getCoverArtwork(),
                            collectionName = dto.collectionName.orEmpty(),
                            trackReleaseYear = dto.getReleaseYear(),
                            primaryGenreName = dto.primaryGenreName.orEmpty(),
                            country = dto.country.orEmpty(),
                            previewUrl = dto.previewUrl.orEmpty(),
                            isFavorite = false
                        )
                    }
                    Result.success(tracks)
                }
            } else {
                Result.failure(Exception())
            }
        }
    }
}
