package com.example.playlistmaker.library.data.db.mappers

import com.example.playlistmaker.library.data.db.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

fun Track.toPlaylistTrackEntity(playlistId: Int): PlaylistTrackEntity {
    return PlaylistTrackEntity(
        playlistId = playlistId,
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
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
