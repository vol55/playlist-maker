package com.example.playlistmaker.library.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    favouriteTracks: List<Track>,
    playlistState: PlaylistsScreenState,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAddPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        stringResource(R.string.favorite_tracks), stringResource(R.string.playlists)
    )

    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
    ) {
        Text(
            text = stringResource(R.string.library),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(horizontal = 16.dp),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onBackground,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.onBackground,
                    width = 148.dp,
                    height = 2.dp
                )
            }) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                        )
                    })
            }
        }

        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> FavoritesScreen(
                    tracks = favouriteTracks.toImmutableList(), onTrackClick = onTrackClick
                )

                1 -> PlaylistsScreen(
                    state = playlistState,
                    onPlaylistClick = onPlaylistClick,
                    onNewPlaylistClick = onAddPlaylistClick
                )
            }
        }
    }
}
