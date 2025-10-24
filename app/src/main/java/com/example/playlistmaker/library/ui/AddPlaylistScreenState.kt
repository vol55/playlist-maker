package com.example.playlistmaker.library.ui

import android.net.Uri
import java.io.File

data class AddPlaylistScreenState(
    val isNameValid: Boolean = false,
    val name: String? = null,
    val description: String? = null,
    val imageUri: Uri? = null,
    val imageFile: File? = null,
    val toastMessage: String? = null,
    val navigateUp: Boolean = false
)
