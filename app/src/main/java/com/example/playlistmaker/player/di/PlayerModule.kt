package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.TrackUi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val playerModule = module {
    viewModel { (track: TrackUi) ->
        PlayerViewModel(
            track, get(), get()
        )
    }
}
