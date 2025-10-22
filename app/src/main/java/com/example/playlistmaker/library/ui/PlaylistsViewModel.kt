package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import kotlinx.coroutines.flow.map


class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    val screenState: LiveData<PlaylistsScreenState> =
        playlistsInteractor.getPlaylists().map { playlists ->
            if (playlists.isEmpty()) PlaylistsScreenState.Empty
            else PlaylistsScreenState.Content(playlists)
        }.asLiveData()
}
