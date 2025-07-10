package com.example.playlistmaker.domain.models

import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) : Parcelable {
    fun getDuration(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.trackTimeMillis)

    fun getCoverArtwork(): String = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getReleaseYear(): String = this.releaseDate.take(4)
}
