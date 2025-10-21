package com.example.playlistmaker.library.domain

import com.example.playlistmaker.player.domain.Playlist


class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }
}
