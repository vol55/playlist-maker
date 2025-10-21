package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.domain.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.library.domain.FavoriteTracksRepository
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.domain.PlaylistsInteractorImpl
import com.example.playlistmaker.library.domain.PlaylistsRepository
import com.example.playlistmaker.library.ui.AddPlaylistViewModel
import com.example.playlistmaker.library.ui.FavoritesViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val libraryModule = module {
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { AddPlaylistViewModel(get()) }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }

    factory<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}
