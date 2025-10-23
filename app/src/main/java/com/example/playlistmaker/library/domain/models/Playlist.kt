package com.example.playlistmaker.library.domain.models

data class Playlist(
    val id: Int,
    val title: String,
    val description: String?,
    val coverImagePath: String?,
    val trackCount: Int
)
