package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

class TracksSearchResponse : Response() {
    @SerializedName("results")
    var results: List<TrackDto> = emptyList()
}
