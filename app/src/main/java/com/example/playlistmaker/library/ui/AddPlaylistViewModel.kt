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

open class AddPlaylistViewModel(
    protected val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    protected val mutableScreenState = MutableLiveData(AddPlaylistScreenState())
    val screenState: LiveData<AddPlaylistScreenState> = mutableScreenState

    fun onNameChanged(name: String) {
        mutableScreenState.value = mutableScreenState.value?.copy(name = name)
        mutableScreenState.value = mutableScreenState.value?.copy(isNameValid = name.isNotBlank())
    }

    fun onDescriptionChanged(description: String) {
        mutableScreenState.value = mutableScreenState.value?.copy(description = description)
    }

    fun onImageSelected(uri: Uri?) {
        if (uri == null) return
        val file = saveCover(uri)

        mutableScreenState.value = mutableScreenState.value?.copy(imageUri = uri)
        mutableScreenState.value = mutableScreenState.value?.copy(imageFile = file)
    }

    fun hasUnsavedChanges(): Boolean {
        return mutableScreenState.value?.isNameValid == true || mutableScreenState.value?.description?.isNotBlank() == true || mutableScreenState.value?.imageUri != null
    }

    fun saveCover(uri: Uri): File? {
        return playlistsInteractor.saveCover(uri)
    }

    suspend fun createPlaylist(): Int {
        val uri = mutableScreenState.value?.imageUri
        val imageFile = uri?.let { saveCover(it) }
        val playlist = Playlist(
            id = 0,
            title = mutableScreenState.value?.name.orEmpty(),
            description = mutableScreenState.value?.description,
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
            context.getString(R.string.add_playlist_track_added, mutableScreenState.value?.name)
        } else {
            context.getString(R.string.add_playlist_created, mutableScreenState.value?.name)
        }
        mutableScreenState.value =
            mutableScreenState.value?.copy(toastMessage = message, navigateUp = true)
    }
}
