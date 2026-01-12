package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                val state by playlistsViewModel.screenState.observeAsState(PlaylistsScreenState.Empty)

                com.example.playlistmaker.root.ui.MyAppTheme {
                    PlaylistsScreen(
                        state = state,
                        onPlaylistClick = {},
                        onNewPlaylistClick = {},
                    )
                }
            }
        }
    }
}
