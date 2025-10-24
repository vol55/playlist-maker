package com.example.playlistmaker.library.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackUi
import java.io.File

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(AddPlaylistScreenState())
    val screenState: LiveData<AddPlaylistScreenState> = _screenState

    fun onNameChanged(name: String) {
        _screenState.value = _screenState.value?.copy(name = name)
        _screenState.value = _screenState.value?.copy(isNameValid = name.isNotBlank())
    }

    fun onDescriptionChanged(description: String) {
        _screenState.value = _screenState.value?.copy(description = description)
    }

    fun onImageSelected(uri: Uri?, context: Context) {
        if (uri == null) return
        val file = saveCover(uri)

        _screenState.value = _screenState.value?.copy(imageUri = uri)
        _screenState.value = _screenState.value?.copy(imageFile = file)
    }

    fun hasUnsavedChanges(): Boolean {
        return _screenState.value?.isNameValid == true || _screenState.value?.description?.isNotBlank() == true || _screenState.value?.imageUri != null
    }

    fun saveCover(uri: Uri): File? {
        return playlistsInteractor.saveCover(uri)
    }

    suspend fun createPlaylist(): Int {
        val uri = _screenState.value?.imageUri
        val imageFile = uri?.let { saveCover(it) }
        val playlist = Playlist(
            id = 0,
            title = _screenState.value?.name.orEmpty(),
            description = _screenState.value?.description,
            coverImagePath = imageFile?.absolutePath,
            trackCount = 0
        )

        return playlistsInteractor.addPlaylist(playlist)
    }

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int) {
        playlistsInteractor.addTrack(track, playlistId)
    }

    fun notifyPlaylistCreated(context: Context, track: TrackUi?) {
        val message = if (track != null) {
            context.getString(R.string.add_playlist_track_added, _screenState.value?.name)
        } else {
            context.getString(R.string.add_playlist_created, _screenState.value?.name)
        }
        _screenState.value = _screenState.value?.copy(toastMessage = message, navigateUp = true)
    }
}
