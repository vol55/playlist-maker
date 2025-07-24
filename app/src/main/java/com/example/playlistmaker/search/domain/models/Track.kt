package com.example.playlistmaker.search.domain.models


data class Track(
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
