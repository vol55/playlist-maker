package com.example.playlistmaker.search.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    val trackName: String,
    val artistName: String,
    val trackDuration: String,
    val coverArtworkUrl: String,
    val collectionName: String,
    val trackReleaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable
