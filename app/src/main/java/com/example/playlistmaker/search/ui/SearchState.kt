package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    data class UpdateSearchHistory(val history: List<Track>) : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data object Loading : SearchState
    data object NoResults : SearchState
    data object NotConnected : SearchState
    data object SearchHistory : SearchState
}
