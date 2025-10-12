package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _favoriteTracks = MutableLiveData<List<Track>>()
    val favoriteTracks: LiveData<List<Track>> = _favoriteTracks

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getTracks().collect { tracks ->
                _favoriteTracks.postValue(tracks)
            }
        }
    }
}
