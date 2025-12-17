package com.example.playlistmaker.player.service

import kotlinx.coroutines.flow.StateFlow

interface MusicServiceInterface {
    val playerState: StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun showNotification(trackName: String, artistName: String)
    fun hideNotification()
}
