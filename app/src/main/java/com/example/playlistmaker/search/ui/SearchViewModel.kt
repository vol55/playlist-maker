package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.History(emptyList()))
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText = ""

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY, viewModelScope, true
    ) { query ->
        if (latestSearchText != query) {
            performSearch(query)
        }
    }

    fun setSearchFieldValue(value: String) {
        latestSearchText = value
    }

    fun searchDebounce(query: String) {
        trackSearchDebounce(query)
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isEmpty()) {
            updateHistory()
        }
        searchDebounce(query)
    }

    fun performSearch(query: String) {
        latestSearchText = query
        stateLiveData.postValue(SearchState.Loading)

        viewModelScope.launch {
            tracksInteractor.searchTracks(query).collect { result ->
                if (result.isSuccess) {
                    val tracks = result.getOrThrow()
                    if (tracks.isEmpty()) {
                        stateLiveData.postValue(SearchState.NoResults)
                    } else {
                        stateLiveData.postValue(SearchState.Results(tracks))
                    }
                } else {
                    stateLiveData.postValue(SearchState.NotConnected)
                }
            }
        }
    }

    fun updateHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                val tracks = searchHistory ?: emptyList()
                stateLiveData.postValue(SearchState.History(tracks))
            }
        })
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
        updateHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        updateHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
