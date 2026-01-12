package com.example.playlistmaker.library.ui

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import java.io.File

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
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackCountText = pluralStringResource(
        id = R.plurals.track_count, count = playlist.trackCount, playlist.trackCount
    )
    val cover = stringResource(R.string.cover)

    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        contentDescription = cover
                    }
                },
                update = { imageView ->
                    val path = playlist.coverImagePath
                    if (path.isNullOrBlank()) {
                        imageView.setImageResource(R.drawable.placeholder)
                    } else {
                        imageView.setImageURI(Uri.fromFile(File(path)))
                    }
                })
        }

        Text(
            text = playlist.title,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = trackCountText,
            modifier = Modifier.padding(top = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
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
