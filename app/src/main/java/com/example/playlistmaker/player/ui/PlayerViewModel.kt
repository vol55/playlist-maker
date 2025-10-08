package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteTracksInteractorImpl
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
    private val favoriteTracksInteractorImpl: FavoriteTracksInteractorImpl
) : ViewModel() {

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }

    private val screenStateLiveData =
        MutableLiveData(PlayerScreenState(isFavorite = track.isFavorite))
    val screenState: LiveData<PlayerScreenState> = screenStateLiveData

    private var timerJob: Job? = null


    init {
        preparePlayer()
        viewModelScope.launch {
            favoriteTracksInteractorImpl.getTracks().collect { favoriteTracks ->
                    val isTrackInDb = favoriteTracks.any { it.trackId == track.trackId }
                    updateState { copy(isFavorite = isTrackInDb) }
                }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentFavorite = screenStateLiveData.value?.isFavorite ?: false

            if (currentFavorite) {
                favoriteTracksInteractorImpl.removeTrack(track.toDomain())
            } else {
                favoriteTracksInteractorImpl.addTrack(track.toDomain())
            }

            updateState { copy(isFavorite = !currentFavorite) }
        }
    }


    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when (screenStateLiveData.value?.playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
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
            while (screenStateLiveData.value?.playerState == PlayerState.PLAYING) {
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

    fun pausePlayerIfNeeded() {
        if (screenStateLiveData.value?.playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    private fun updateState(update: PlayerScreenState.() -> PlayerScreenState) {
        screenStateLiveData.value = (screenStateLiveData.value ?: PlayerScreenState()).update()
    }

    companion object {
        private const val DELAY = 300L
    }
}