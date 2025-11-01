package com.example.playlistmaker.search.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Int? = null,
    val artworkUrl100: String? = null,
    val coverArtworkUrl: String? = null,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null,
) {
    fun getDuration(): String =
        trackTimeMillis?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(it) }
            ?: "00:00"

    fun getCoverArtwork(): String = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""

    fun getReleaseYear(): String = releaseDate?.take(4) ?: ""
}