package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractor(
    private val repository: SearchHistoryRepository
) {
    fun saveTrack(track: Track) = repository.saveTrack(track)
    fun getTracks(): List<Track> = repository.getTracks()
    fun clear() = repository.clear()
}
