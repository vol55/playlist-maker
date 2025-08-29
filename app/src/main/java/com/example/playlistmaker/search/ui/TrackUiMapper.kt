package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

fun Track.toUi(): TrackUi {
    return TrackUi(
        trackName = trackName,
        artistName = artistName,
        trackDuration = trackDuration,
        coverArtworkUrl = coverArtworkUrl,
        collectionName = collectionName,
        trackReleaseYear = trackReleaseYear,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}
