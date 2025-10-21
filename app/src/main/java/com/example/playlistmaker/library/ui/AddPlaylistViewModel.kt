package com.example.playlistmaker.library.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.player.domain.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var _name: String = ""
    val name: String get() = _name
    private var _description: String = ""
    private var _imageUri: Uri? = null

    private val _screenState =
        MutableLiveData<AddPlaylistScreenState>(AddPlaylistScreenState.EmptyName)
    val screenState: LiveData<AddPlaylistScreenState> = _screenState

    fun onNameChanged(name: String) {
        _name = name
        updateScreenState()
    }

    fun onDescriptionChanged(description: String) {
        _description = description
    }

    fun onImageSelected(uri: Uri?) {
        _imageUri = uri
    }

    private fun updateScreenState() {
        _screenState.value = if (_name.isNotBlank()) {
            AddPlaylistScreenState.ValidName
        } else {
            AddPlaylistScreenState.EmptyName
        }
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

    fun createPlaylist(context: Context) {
        viewModelScope.launch {
            val imageFile = saveImageToPrivateStorage(context)
            val playlist = Playlist(
                id = 0,
                title = _name,
                description = _description,
                coverImagePath = imageFile?.absolutePath,
                trackIds = emptyList(),
                trackCount = 0
            )
            playlistsInteractor.addPlaylist(playlist)
        }
    }
}
