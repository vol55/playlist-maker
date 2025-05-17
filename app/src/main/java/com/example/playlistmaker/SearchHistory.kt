package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import androidx.core.content.edit


class SearchHistory(context: Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTrack(track: Track) {
        val tracks = getTracks()

        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)

        if (tracks.size > 10) {
            tracks.subList(10, tracks.size).clear()
        }

        prefs.edit() { putString("tracks", gson.toJson(tracks)) }
    }

    fun getTracks(): MutableList<Track> {
        val json = prefs.getString("tracks", null)
        return when (json) {
            null -> mutableListOf()
            else -> Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        }
    }

    fun clear() {
        prefs.edit() { remove("tracks") }
    }
}
