package com.example.playlistmaker.search.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.root.ui.MyAppTheme
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentQuery = savedInstanceState?.getString(SEARCH_FIELD_VALUE).orEmpty()

        viewModel.setScreenState(currentQuery)

        val onTrackClickDebounced: (Track) -> Unit = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            viewModel.saveTrackToHistory(track)
            val args = PlayerFragment.createArgs(track.toUi())
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment, args)
        }

        (view as ComposeView).setContent {
            val state by viewModel.screenState.observeAsState()

            var query by rememberSaveable { mutableStateOf(currentQuery) }

            MyAppTheme {
                SearchScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    query = query,
                    onQueryChange = { newQuery ->
                        query = newQuery
                        currentQuery = query
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    onClearQuery = {
                        query = ""
                        currentQuery = query
                        viewModel.onSearchQueryChanged("")
                        viewModel.setScreenStateToHistory()
                    },
                    state = state,
                    onDone = {
                        viewModel.performSearch(query)
                    },
                    onRetry = {
                        viewModel.performSearch(query)
                    },
                    onClearHistory = {
                        viewModel.clearHistory()
                        viewModel.setScreenStateToHistory()
                    },
                    onTrackClick = onTrackClickDebounced
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setScreenState(currentQuery)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, currentQuery)
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = "search_field_value"
        private const val CLICK_DEBOUNCE_DELAY = 200L
    }
}
