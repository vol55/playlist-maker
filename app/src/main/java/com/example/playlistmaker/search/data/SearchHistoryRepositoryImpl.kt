package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
) : SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > 10) {
            tracks.subList(10, tracks.size).clear()
        }
        storage.storeData(tracks)
    }

    override fun getHistory(): List<Track> {
        return storage.getData() ?: emptyList()
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }
}
