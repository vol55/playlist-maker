package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson


class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences, private val gson: Gson
) : SearchHistoryRepository {

    override fun saveTrack(track: Track) {
        val tracks = getTracks().toMutableList()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > 10) {
            tracks.subList(10, tracks.size).clear()
        }
        sharedPreferences.edit { putString("tracks", gson.toJson(tracks)) }
    }

    override fun getTracks(): List<Track> {
        return when (val json = sharedPreferences.getString("tracks", null)) {
            null -> emptyList()
            else -> gson.fromJson(json, Array<Track>::class.java).toList()
        }
    }

    override fun clear() {
        sharedPreferences.edit { remove("tracks") }
    }
}
