package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackDuration: String,
    val artworkUrl100: String,
    val coverArtworkUrl: String,
    val collectionName: String,
    val trackReleaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
)
