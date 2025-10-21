package com.example.playlistmaker.player.domain

data class Playlist(
    val id: Int,
    val title: String,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: List<Int>,
    val trackCount: Int
)
