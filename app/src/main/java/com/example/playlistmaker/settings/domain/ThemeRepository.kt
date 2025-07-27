package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(enabled: Boolean)
}
