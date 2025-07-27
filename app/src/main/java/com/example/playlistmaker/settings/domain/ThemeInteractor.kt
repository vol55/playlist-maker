package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun switchTheme(enabled: Boolean)
}
