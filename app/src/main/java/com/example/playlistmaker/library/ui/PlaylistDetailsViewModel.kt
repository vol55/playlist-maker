package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File


class PlaylistDetailsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    data class ScreenState(
        val playlistName: String? = null,
        val imageFile: File? = null,
        val minutes: Int? = null,
        val trackCount: Int? = null,
        val tracks: List<Track> = emptyList(),
    )

    private val _screenState = MutableLiveData<ScreenState>(
        ScreenState()
    )
    val screenState: LiveData<ScreenState> get() = _screenState


    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlistWithTracks = playlistsInteractor.getPlaylistWithTracks(playlistId).first()
            val tracks = playlistWithTracks.tracks

            _screenState.postValue(
                ScreenState(
                    playlistName = playlistWithTracks.playlist.title,
                imageFile = playlistWithTracks.playlist.coverImagePath?.let { File(it) },
                minutes = tracks.sumOf { it.trackTimeMillis } / 1000 / 60,
                trackCount = tracks.size,
                tracks = tracks))
        }
    }
}
