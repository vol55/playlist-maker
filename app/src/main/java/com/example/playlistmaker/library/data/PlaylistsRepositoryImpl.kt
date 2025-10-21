package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.toEntity
import com.example.playlistmaker.library.domain.PlaylistsRepository
import com.example.playlistmaker.player.domain.Playlist


class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlist.toEntity())
    }


}
