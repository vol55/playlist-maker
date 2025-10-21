package com.example.playlistmaker.library.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream

class AddPlaylistViewModel : ViewModel() {

    private var _name: String = ""
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

    fun getImageUri(): Uri? = _imageUri
    fun getName(): String = _name

    fun saveImageToPrivateStorage(context: Context): File? {
        val uri = _imageUri ?: return null

        val filePath =
            File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, "playlist_cover.jpg")
        context.contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input).compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
        }
        return file
    }
}
