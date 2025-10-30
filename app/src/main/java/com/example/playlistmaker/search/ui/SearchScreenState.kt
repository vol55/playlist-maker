package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState {

    data class History(val history: List<Track>) : SearchScreenState
    data class Results(val tracks: List<Track>) : SearchScreenState
    data object Loading : SearchScreenState
    data object NoResults : SearchScreenState
    data object NotConnected : SearchScreenState
}
