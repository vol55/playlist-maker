package com.example.playlistmaker.settings.domain.api

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun switchTheme(enabled: Boolean)
}
