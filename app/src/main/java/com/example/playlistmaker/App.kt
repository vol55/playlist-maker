package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("theme_preferences", MODE_PRIVATE)
        val repository = ThemeRepositoryImpl(prefs)

        themeInteractor = ThemeInteractorImpl(repository) { isDarkThemeEnabled ->
            val mode = if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
        themeInteractor.switchTheme(themeInteractor.isDarkTheme())
    }
}
