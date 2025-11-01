package com.example.playlistmaker.library.data.db.mappers

import com.example.playlistmaker.library.data.db.PlaylistWithTracksEntity
import com.example.playlistmaker.library.domain.models.PlaylistWithTracks


fun PlaylistWithTracksEntity.toDomain(): PlaylistWithTracks {
    return PlaylistWithTracks(
        playlist = playlist.toDomain(), tracks = tracks.map { it.toDomain() })
}
