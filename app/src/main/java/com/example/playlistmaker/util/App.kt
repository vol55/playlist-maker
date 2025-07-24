package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.ThemeInteractor

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.switchTheme(themeInteractor.isDarkTheme())
    }
}