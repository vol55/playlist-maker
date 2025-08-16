package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.ui.FavoritesViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val libraryModule = module {
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
}
