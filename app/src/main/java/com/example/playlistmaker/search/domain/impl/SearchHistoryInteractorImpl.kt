package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        val history = repository.getHistory()
        consumer.consume(history)
    }

    override fun saveToHistory(m: Track) {
        repository.saveToHistory(m)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
