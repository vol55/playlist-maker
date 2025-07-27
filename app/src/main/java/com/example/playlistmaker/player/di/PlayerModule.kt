package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.ui.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }

    viewModel { (url: String) ->
        PlayerViewModel(
            url,
            playerInteractor = get()
        )
    }
}
