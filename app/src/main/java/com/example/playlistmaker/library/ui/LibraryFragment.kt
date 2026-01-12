package com.example.playlistmaker.library.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.root.ui.MyAppTheme
import com.example.playlistmaker.search.ui.toUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val favouriteTracks by favoritesViewModel.favoriteTracks.observeAsState(emptyList())
                val playlistState by playlistsViewModel.screenState.observeAsState(
                    PlaylistsScreenState.Empty
                )

                LaunchedEffect(Unit) {
                    favoritesViewModel.loadFavoriteTracks()
                }

                MyAppTheme {
                    LibraryScreen(
                        favouriteTracks = favouriteTracks,
                        playlistState = playlistState,
                        onTrackClick = { track ->
                            val args = PlayerFragment.createArgs(track.toUi())
                            findNavController().navigate(
                                R.id.action_libraryFragment_to_playerFragment, args
                            )
                        },
                        onPlaylistClick = { playlist ->
                            findNavController().navigate(
                                R.id.action_libraryFragment_to_playlistDetailsFragment,
                                createArgs(playlist.id)
                            )
                        },
                        onAddPlaylistClick = {
                            findNavController().navigate(
                                R.id.action_libraryFragment_to_addPlaylistFragment
                            )
                        })
                }
            }
        }
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"
        fun createArgs(playlistId: Int): Bundle = bundleOf(ARG_PLAYLIST_ID to playlistId)
    }
}
