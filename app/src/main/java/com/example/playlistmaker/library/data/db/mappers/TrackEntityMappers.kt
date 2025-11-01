package com.example.playlistmaker.library.data.db.mappers


import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        trackDuration = trackDuration,
        artworkUrl100 = artworkUrl100,
        coverArtworkUrl = coverArtworkUrl,
        collectionName = collectionName,
        trackReleaseYear = trackReleaseYear,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}

fun TrackEntity.toDomain(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        trackDuration = trackDuration,
        artworkUrl100 = artworkUrl100,
        coverArtworkUrl = coverArtworkUrl,
        collectionName = collectionName,
        trackReleaseYear = trackReleaseYear,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        isFavorite = true
    )
}
