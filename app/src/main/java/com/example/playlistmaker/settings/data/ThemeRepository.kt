package com.example.playlistmaker.settings.data

interface ThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(enabled: Boolean)
}
