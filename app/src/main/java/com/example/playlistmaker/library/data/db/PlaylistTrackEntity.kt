package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_tracks", foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("playlistId")]
)
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playlistId: Int,
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackDuration: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val coverArtworkUrl: String,
    val collectionName: String,
    val trackReleaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)
