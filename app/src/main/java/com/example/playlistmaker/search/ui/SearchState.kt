package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    data class History(val history: List<Track>) : SearchState
    data class Results(val tracks: List<Track>) : SearchState
    data object Loading : SearchState
    data object NoResults : SearchState
    data object NotConnected : SearchState
}
