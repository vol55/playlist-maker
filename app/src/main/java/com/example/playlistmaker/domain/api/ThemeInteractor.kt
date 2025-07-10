package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun switchTheme(enabled: Boolean)
}
