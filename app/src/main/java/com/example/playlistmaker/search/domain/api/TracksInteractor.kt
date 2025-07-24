package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    suspend fun searchTracks(expression: String): Result<List<Track>>
}
