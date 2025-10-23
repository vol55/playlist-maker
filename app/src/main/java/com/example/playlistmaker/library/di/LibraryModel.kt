package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.api.PlaylistsRepository
import com.example.playlistmaker.library.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.library.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.library.ui.AddPlaylistViewModel
import com.example.playlistmaker.library.ui.FavoritesViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val libraryModule = module {
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { AddPlaylistViewModel(get()) }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }

    factory<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}
