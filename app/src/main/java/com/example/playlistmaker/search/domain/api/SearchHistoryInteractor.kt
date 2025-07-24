package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(m: Track)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }
}
