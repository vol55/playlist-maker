package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _screenState =
        MutableLiveData<SearchScreenState>(SearchScreenState.History(emptyList()))

    val screenState: LiveData<SearchScreenState> get() = _screenState

    private var searchJob: Job? = null
    private var lastQuery: String = ""

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank() || query == lastQuery) return

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    fun performSearch(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            setScreenStateToHistory()
            return
        }
        _screenState.postValue(SearchScreenState.Loading)

        viewModelScope.launch {
            lastQuery = query
            tracksInteractor.searchTracks(query).collect { result ->
                if (result.isSuccess) {
                    val tracks = result.getOrThrow()
                    if (tracks.isEmpty()) {
                        _screenState.postValue(SearchScreenState.NoResults)
                    } else {
                        _screenState.postValue(SearchScreenState.Results(tracks))
                    }
                } else {
                    _screenState.postValue(SearchScreenState.NotConnected)
                }
            }
        }
    }

    fun setScreenStateToHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                val tracks = searchHistory ?: emptyList()
                _screenState.postValue(SearchScreenState.History(tracks))
            }
        })
    }

    fun setScreenState(query: String) {
        when (_screenState.value) {
            is SearchScreenState.Results -> {}
            is SearchScreenState.NoResults -> {}
            else -> {
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    setScreenStateToHistory()
                }
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
