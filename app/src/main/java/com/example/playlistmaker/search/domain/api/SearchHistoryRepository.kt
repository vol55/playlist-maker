package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}