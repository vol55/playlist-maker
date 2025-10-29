package com.example.playlistmaker.library.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class PlaylistDetailsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    data class ScreenState(
        val playlistName: String? = null,
        val imageFile: File? = null,
        val description: String? = null,
        val minutes: Int? = null,
        val trackCount: Int? = null,
        val tracks: List<Track> = emptyList()
    )

    private val _screenState = MutableLiveData(ScreenState())
    val screenState: LiveData<ScreenState> get() = _screenState

    private val shareIntentEvent = SingleLiveEvent<Intent>()
    fun observeShareIntent(): LiveData<Intent> = shareIntentEvent

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlistWithTracks = playlistsInteractor.getPlaylistWithTracks(playlistId).first()
            val tracks = playlistWithTracks.tracks

            _screenState.postValue(
                ScreenState(
                playlistName = playlistWithTracks.playlist.title,
                imageFile = playlistWithTracks.playlist.coverImagePath?.let { File(it) },
                description = playlistWithTracks.playlist.description,
                minutes = tracks.sumOf { it.trackTimeMillis } / 1000 / 60,
                trackCount = tracks.size,
                tracks = tracks
                )
            )
        }
    }

    suspend fun removeTrack(playlistId: Int, track: Track) {
        playlistsInteractor.removeTrack(playlistId, track.trackId)
    }

    suspend fun removePlaylist(playlistId: Int) {
        playlistsInteractor.removePlaylist(playlistId)
    }

    fun sharePlaylist(shareText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        shareIntentEvent.postValue(Intent.createChooser(intent, null))
    }
}
