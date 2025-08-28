package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }

    companion object {
        private const val DELAY = 500L
    }

    private val screenStateLiveData = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = screenStateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable: Runnable = Runnable {
        if (screenStateLiveData.value?.playerState == PlayerState.PLAYING) {
            updateProgress()
            handler.postDelayed(timerRunnable, DELAY)
        }
    }

    init {
        preparePlayer()
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
        playerInteractor.prepare(url, {
            screenStateLiveData.value = PlayerScreenState(
                playerState = PlayerState.PREPARED,
                progressTime = screenStateLiveData.value?.progressTime ?: "00:00"
            )
        }, {
            screenStateLiveData.value = PlayerScreenState(
                playerState = PlayerState.PREPARED, progressTime = "00:00"
            )
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
        handler.removeCallbacks(timerRunnable)
        updateProgress()
        handler.postDelayed(timerRunnable, DELAY)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
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
}
