package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.player.service.MusicServiceInterface
import com.example.playlistmaker.player.service.PlayerState
import com.example.playlistmaker.search.ui.TrackUi
import com.example.playlistmaker.search.ui.toDomain
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: TrackUi,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private var musicServiceInterface: MusicServiceInterface? = null
) : ViewModel() {

    private val _screenState = MutableLiveData(
        PlayerScreenState(
            isFavorite = track.isFavorite, playerState = PlayerState.Default()
        )
    )
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

    fun onPlayPauseClicked() {
        val currentState = _screenState.value?.playerState
        when (currentState) {
            is PlayerState.Prepared, is PlayerState.Paused -> musicServiceInterface?.startPlayer()
            is PlayerState.Playing -> musicServiceInterface?.pausePlayer()
            else -> {}
        }
    }

    fun setPlayerState(state: PlayerState) {
        updateState { copy(playerState = state) }
    }

    private fun updateState(update: PlayerScreenState.() -> PlayerScreenState) {
        _screenState.value = (_screenState.value ?: PlayerScreenState()).update()
    }

    fun bindMusicService(service: MusicServiceInterface) {
        musicServiceInterface = service
    }

    fun showNotification() {
        musicServiceInterface?.showNotification()
    }

    fun hideNotification() {
        musicServiceInterface?.hideNotification()
    }

    fun updateNotification(state: PlayerState) {
        if (state is PlayerState.Playing) {
            showNotification()
        } else {
            hideNotification()
        }
    }
}
