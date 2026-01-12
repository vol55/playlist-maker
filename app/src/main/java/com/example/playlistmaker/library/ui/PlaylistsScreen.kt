package com.example.playlistmaker.library.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.PlaylistItem

@Composable
fun PlaylistsScreen(
    state: PlaylistsScreenState,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = onNewPlaylistClick,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
            )
        }

        when (state) {
            PlaylistsScreenState.Empty -> EmptyPlaylists(
                onNewPlaylistClick = onNewPlaylistClick, modifier = modifier
            )

            is PlaylistsScreenState.Content -> PlaylistsGrid(
                playlists = state.playlists, onPlaylistClick = onPlaylistClick, modifier = modifier
            )
        }
    }
}

@Composable
fun PlaylistsGrid(
    playlists: List<Playlist>, onPlaylistClick: (Playlist) -> Unit, modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        items(
            items = playlists, key = { it.id }) { playlist ->
            PlaylistItem(
                playlist = playlist, onClick = { onPlaylistClick(playlist) })
        }
    }
}

@Composable
fun EmptyPlaylists(
    onNewPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val imageRes = if (isDark) R.drawable.no_results_dark else R.drawable.no_results_light

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            painter = painterResource(imageRes),
            contentDescription = stringResource(R.string.something_went_wrong),
            modifier = Modifier
                .padding(top = 0.dp)
                .size(96.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(R.string.no_playlists_created),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 19.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
