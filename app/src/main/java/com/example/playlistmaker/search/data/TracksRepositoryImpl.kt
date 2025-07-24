package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override suspend fun searchTracks(expression: String): Response {
        return networkClient.doRequest(TracksSearchRequest(expression))
    }
}
