package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        private const val DELAY = 500L

        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val playerInteractor: PlayerInteractor = Creator.providePlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
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
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        playerInteractor.prepare(url, {
            playerStateLiveData.postValue(STATE_PREPARED)
        }, {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        })
    }

    private fun startPlayer() {
        playerInteractor.play()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        val currentPosition = playerInteractor.getCurrentPosition()
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
        )
        handler.postDelayed(timerRunnable, DELAY)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
    }

}
