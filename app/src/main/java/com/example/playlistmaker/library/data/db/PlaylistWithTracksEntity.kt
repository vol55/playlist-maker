package com.example.playlistmaker.library.data.db

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithTracksEntity(
    @Embedded val playlist: PlaylistEntity, @Relation(
        parentColumn = "id", entityColumn = "playlistId"
    ) val tracks: List<PlaylistTrackEntity>
)
