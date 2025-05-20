package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("theme_preferences", MODE_PRIVATE)
        darkTheme = prefs.getBoolean("dark_theme", false)
        applyTheme(darkTheme)
    }

    fun switchTheme(enabled: Boolean) {
        darkTheme = enabled
        val prefs = getSharedPreferences("theme_preferences", MODE_PRIVATE)
        prefs.edit() { putBoolean("dark_theme", enabled) }
        applyTheme(enabled)
    }

    private fun applyTheme(enabled: Boolean) {
        val mode =
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
