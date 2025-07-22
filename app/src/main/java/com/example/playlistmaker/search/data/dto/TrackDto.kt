package com.example.playlistmaker.search.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val coverArtworkUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) {
    fun getDuration(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.trackTimeMillis)

    fun getCoverArtwork(): String = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getReleaseYear(): String = this.releaseDate.take(4)
}
