package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.ui.TrackUi
import com.example.playlistmaker.search.ui.toDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: TrackUi,
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    enum class PlayerState { DEFAULT, PREPARED, PLAYING, PAUSED }

    private val _screenState = MutableLiveData(PlayerScreenState(isFavorite = track.isFavorite))
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var timerJob: Job? = null

    init {
        preparePlayer()
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

    fun onPlayButtonClicked() {
        when (_screenState.value?.playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    fun pausePlayerIfNeeded() {
        if (_screenState.value?.playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    private fun preparePlayer() {
        playerInteractor.prepare(track.previewUrl, {
            updateState { copy(playerState = PlayerState.PREPARED) }
        }, {
            updateState { copy(playerState = PlayerState.PREPARED, progressTime = "00:00") }
        })
    }

    private fun startPlayer() {
        playerInteractor.play()
        updateState { copy(playerState = PlayerState.PLAYING) }
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pause()
        updateState { copy(playerState = PlayerState.PAUSED) }
        pauseTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_screenState.value?.playerState == PlayerState.PLAYING) {
                updateProgress()
                delay(DELAY)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resetTimer() {
        pauseTimer()
        updateState { copy(progressTime = "00:00") }
    }

    private fun updateProgress() {
        val currentPosition = playerInteractor.getCurrentPosition()
        updateState {
            copy(
                progressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                    currentPosition
                )
            )
        }
    }

    private fun updateState(update: PlayerScreenState.() -> PlayerScreenState) {
        _screenState.value = (_screenState.value ?: PlayerScreenState()).update()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        resetTimer()
    }

    companion object {
        private const val DELAY = 300L
    }
}
