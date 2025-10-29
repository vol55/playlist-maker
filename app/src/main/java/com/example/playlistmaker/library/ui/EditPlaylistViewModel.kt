package com.example.playlistmaker.library.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    playlistsInteractor: PlaylistsInteractor
) : AddPlaylistViewModel(playlistsInteractor) {

    fun initWithPlaylistId(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylist(playlistId).first()
            val currentState = mutableScreenState.value ?: AddPlaylistScreenState()

            mutableScreenState.value = currentState.copy(
                name = playlist.title,
                description = playlist.description,
                imageFile = playlist.coverImagePath?.let { File(it) },
            )
        }
    }

    fun notifyPlaylistEdited(context: Context) {
        mutableScreenState.value = mutableScreenState.value?.copy(
            toastMessage = context.getString(R.string.data_saved), navigateUp = true
        )
    }

    suspend fun editPlaylist(playlistId: Int) {
        val trackCount = playlistsInteractor.getPlaylist(playlistId).first().trackCount
        val editedPlaylist = Playlist(
            id = playlistId,
            title = mutableScreenState.value?.name.orEmpty(),
            description = mutableScreenState.value?.description,
            coverImagePath = mutableScreenState.value?.imageFile?.absolutePath,
            trackCount = trackCount
        )

        return playlistsInteractor.updatePlaylist(editedPlaylist)
    }
}
