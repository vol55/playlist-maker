package com.example.playlistmaker.library.ui

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    playlistsInteractor: PlaylistsInteractor
) : AddPlaylistViewModel(playlistsInteractor) {

    fun initWithPlaylistId(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylist(playlistId).first()
            val currentState = mutableScreenState.value ?: AddPlaylistScreenState()

            mutableScreenState.value = currentState.copy(
                name = playlist.title,
                description = playlist.description,
                imageFile = playlist.coverImagePath?.let { File(it) },
            )
        }
    }
}
