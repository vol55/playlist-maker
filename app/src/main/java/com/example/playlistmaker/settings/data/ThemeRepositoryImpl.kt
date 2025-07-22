package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit

class ThemeRepositoryImpl(private val prefs: SharedPreferences) : ThemeRepository {
    override fun getTheme(): Boolean = prefs.getBoolean("dark_theme", false)

    override fun saveTheme(enabled: Boolean) {
        prefs.edit { putBoolean("dark_theme", enabled) }
    }
}
