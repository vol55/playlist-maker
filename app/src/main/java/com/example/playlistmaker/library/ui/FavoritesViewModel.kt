package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTracksInteractorImpl: FavoriteTracksInteractorImpl
) : ViewModel() {

    private val _favoriteTracks = MutableLiveData<List<Track>>()
    val favoriteTracks: LiveData<List<Track>> = _favoriteTracks

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractorImpl.getTracks().collect { tracks ->
                _favoriteTracks.postValue(tracks)
            }
        }
    }
}
