package com.example.playlistmaker.library.ui

sealed class AddPlaylistScreenState {
    object EmptyName : AddPlaylistScreenState()
    object ValidName : AddPlaylistScreenState()
}
