package com.example.playlistmaker.library.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.FileOutputStream

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var _name = ""
    private var _description = ""
    private var _imageUri: Uri? = null

    private val _screenState = MutableLiveData(AddPlaylistScreenState())
    val screenState: LiveData<AddPlaylistScreenState> = _screenState

    fun onNameChanged(name: String) {
        _name = name
        _screenState.value = _screenState.value?.copy(isNameValid = name.isNotBlank())
    }

    fun onDescriptionChanged(description: String) {
        _description = description
    }

    fun onImageSelected(uri: Uri?, context: Context) {
        _imageUri = uri
        val file = saveImageToPrivateStorage(context)
        _screenState.value = _screenState.value?.copy(imageFile = file)
    }

    fun hasUnsavedChanges(): Boolean {
        return _name.isNotBlank() || _description.isNotBlank() || _imageUri != null
    }

    fun saveImageToPrivateStorage(context: Context): File? {
        val uri = _imageUri ?: return null
        val filePath = File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "playlists"
        )
        if (!filePath.exists()) filePath.mkdirs()
        val file = File(filePath, "playlist_cover_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input).compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
        }
        return file
    }

    suspend fun createPlaylist(context: Context): Int {
        val imageFile = saveImageToPrivateStorage(context)
        val playlist = Playlist(
            id = 0,
            title = _name,
            description = _description,
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
            context.getString(R.string.add_playlist_track_added, _name)
        } else {
            context.getString(R.string.add_playlist_created, _name)
        }
        _screenState.value = _screenState.value?.copy(toastMessage = message, navigateUp = true)
    }
}
