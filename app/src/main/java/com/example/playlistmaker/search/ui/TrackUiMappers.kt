package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

fun Track.toUi(): TrackUi {
    return TrackUi(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackDuration = trackDuration,
        coverArtworkUrl = coverArtworkUrl,
        collectionName = collectionName,
        trackReleaseYear = trackReleaseYear,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        isFavorite = isFavorite
    )
}

fun TrackUi.toDomain(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackDuration = trackDuration,
        artworkUrl100 = coverArtworkUrl,
        coverArtworkUrl = coverArtworkUrl,
        collectionName = collectionName,
        trackReleaseYear = trackReleaseYear,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        isFavorite = isFavorite
    )
}
