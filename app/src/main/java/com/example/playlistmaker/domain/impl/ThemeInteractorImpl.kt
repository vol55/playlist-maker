package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.ThemeRepository
import com.example.playlistmaker.domain.api.ThemeInteractor

class ThemeInteractorImpl(
    private val repository: ThemeRepository, private val applyThemeFunc: (Boolean) -> Unit
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean = repository.getTheme()

    override fun switchTheme(enabled: Boolean) {
        repository.saveTheme(enabled)
        applyThemeFunc(enabled)
    }
}
