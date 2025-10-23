package com.example.playlistmaker.library.ui

import java.io.File

data class AddPlaylistScreenState(
    val isNameValid: Boolean = false,
    val imageFile: File? = null,
    val toastMessage: String? = null,
    val navigateUp: Boolean = false
)
