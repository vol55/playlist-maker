package com.example.playlistmaker.library.data.db.mappers

import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.domain.models.Playlist

fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = id,
        title = title,
        description = description,
        coverImagePath = coverImagePath,
        trackCount = trackCount
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        title = title,
        description = description,
        coverImagePath = coverImagePath,
        trackCount = trackCount
    )
}
