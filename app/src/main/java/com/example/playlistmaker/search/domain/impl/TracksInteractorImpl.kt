package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override suspend fun searchTracks(expression: String): Result<List<Track>> {
        val response = repository.searchTracks(expression)

        return if (response is TracksSearchResponse) {
            if (response.resultCode == -1) {
                Result.failure(Exception())
            } else {
                val tracks = response.results.map { dto: TrackDto ->
                    Track(
                        dto.trackId,
                        dto.trackName,
                        dto.artistName,
                        dto.getDuration(),
                        dto.artworkUrl100,
                        dto.getCoverArtwork(),
                        dto.collectionName,
                        dto.getReleaseYear(),
                        dto.primaryGenreName,
                        dto.country,
                        dto.previewUrl,
                    )
                }
                Result.success(tracks)
            }
        } else {
            Result.failure(Exception())
        }
    }
}
