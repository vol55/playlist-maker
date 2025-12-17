package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.search.ui.TrackUi
import com.example.playlistmaker.search.ui.toDomain
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: TrackUi,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState(isFavorite = track.isFavorite))
    val screenState: LiveData<PlayerScreenState> = _screenState

    init {
        viewModelScope.launch {
            val isFavorite = favoriteTracksInteractor.isFavorite(track.trackId)
            updateState { copy(isFavorite = isFavorite) }
        }

        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                updateState { copy(playlists = playlists) }
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentFavorite = _screenState.value?.isFavorite ?: false
            if (currentFavorite) {
                favoriteTracksInteractor.removeTrack(track.toDomain())
            } else {
                favoriteTracksInteractor.addTrack(track.toDomain())
            }
            updateState { copy(isFavorite = !currentFavorite) }
        }
    }

    fun addTrackToPlaylist(playlistId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val trackDomain = track.toDomain()
            val exists = playlistsInteractor.isTrackInPlaylist(playlistId, trackDomain.trackId)
            if (exists) {
                onResult(false)
            } else {
                playlistsInteractor.addTrack(trackDomain, playlistId)
                onResult(true)
            }
        }
    }

    private fun updateState(update: PlayerScreenState.() -> PlayerScreenState) {
        _screenState.value = (_screenState.value ?: PlayerScreenState()).update()
    }
}
