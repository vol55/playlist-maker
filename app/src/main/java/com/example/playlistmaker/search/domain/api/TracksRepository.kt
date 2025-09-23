package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Response
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Response>
}
