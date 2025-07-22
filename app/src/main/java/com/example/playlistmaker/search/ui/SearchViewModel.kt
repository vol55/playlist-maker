package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getFactory(
            tracksInteractor: TracksInteractor, searchHistoryInteractor: SearchHistoryInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(tracksInteractor, searchHistoryInteractor)
            }
        }
    }

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.SearchHistory)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val openPlayerLiveData = SingleLiveEvent<Track>()
    fun observeOpenPlayer(): LiveData<Track> = openPlayerLiveData

    fun onTrackClicked(track: Track) {
        openPlayerLiveData.setValue(track)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var isClickAllowed = true
    private var searchFieldValue = ""

    fun setSearchFieldValue(value: String) {
        searchFieldValue = value
    }

    fun onSearchQueryChanged(query: String, delay: Long = SEARCH_DEBOUNCE_DELAY) {
        searchFieldValue = query
        searchRunnable?.let { handler.removeCallbacks(it) }

        if (query.isEmpty()) {
            stateLiveData.postValue(SearchState.SearchHistory)
            return
        }

        searchRunnable = Runnable {
            performSearch(query)
        }
        handler.postDelayed(searchRunnable!!, delay)
    }

    private fun performSearch(query: String) {
        stateLiveData.postValue(SearchState.Loading)

        viewModelScope.launch {
            val result = tracksInteractor.searchTracks(query)
            if (result.isSuccess) {
                val tracks = result.getOrThrow()
                if (tracks.isEmpty()) {
                    stateLiveData.postValue(SearchState.NoResults)
                } else {
                    stateLiveData.postValue(SearchState.Content(tracks))
                }
            } else {
                stateLiveData.postValue(SearchState.NotConnected)
            }
        }
    }

    fun updateHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                val tracks = searchHistory ?: emptyList()
                stateLiveData.postValue(SearchState.UpdateSearchHistory(tracks))
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

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }
}
