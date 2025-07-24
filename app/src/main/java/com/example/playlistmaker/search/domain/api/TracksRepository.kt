package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Response

interface TracksRepository {
    suspend fun searchTracks(expression: String): Response
}
