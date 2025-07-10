package com.example.playlistmaker.data

interface ThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(enabled: Boolean)
}
