package com.example.playlistmaker.settings.domain

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {

    override fun isDarkTheme(): Boolean = repository.getTheme()

    override fun switchTheme(enabled: Boolean) {
        repository.saveTheme(enabled)
    }
}
