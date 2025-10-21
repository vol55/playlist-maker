package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.player.domain.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()
private val listType = object : TypeToken<List<Int>>() {}.type

fun PlaylistEntity.toDomain(): Playlist {
    val trackIds: List<Int> = gson.fromJson(trackIdsJson, listType) ?: emptyList()
    return Playlist(
        id = id,
        title = title,
        description = description,
        coverImagePath = coverImagePath,
        trackIds = trackIds,
        trackCount = trackCount
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    val trackIdsJson = gson.toJson(trackIds)
    return PlaylistEntity(
        id = id,
        title = title,
        description = description,
        coverImagePath = coverImagePath,
        trackIdsJson = trackIdsJson,
        trackCount = trackCount
    )
}
