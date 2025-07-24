package com.example.playlistmaker.settings.domain

class ThemeInteractorImpl(
    private val repository: ThemeRepository, private val applyThemeFunc: (Boolean) -> Unit
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean = repository.getTheme()

    override fun switchTheme(enabled: Boolean) {
        repository.saveTheme(enabled)
        applyThemeFunc(enabled)
    }
}