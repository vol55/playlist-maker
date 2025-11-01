package com.example.playlistmaker.library.domain.models

import com.example.playlistmaker.search.domain.models.Track

data class PlaylistWithTracks(
    val playlist: Playlist, val tracks: List<Track>
)
