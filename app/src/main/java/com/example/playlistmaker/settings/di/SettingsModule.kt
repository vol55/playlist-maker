package com.example.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    factory<ThemeRepository> { ThemeRepositoryImpl(get()) }
    factory<ThemeInteractor> { ThemeInteractorImpl(get()) }

    viewModel { SettingsViewModel(get()) }
}
