package com.example.playlistmaker.library.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackLazyColumn
import kotlinx.collections.immutable.ImmutableList


@Composable
fun FavoritesScreen(
    tracks: ImmutableList<Track>, onTrackClick: (Track) -> Unit, modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) {
        EmptyFavorites(modifier = modifier.fillMaxSize())
    } else {

        TrackLazyColumn(
            tracks = tracks, onTrackClick = onTrackClick, modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun EmptyFavorites(
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val imageRes = if (isDark) R.drawable.no_results_dark else R.drawable.no_results_light

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = stringResource(R.string.something_went_wrong),
            modifier = Modifier
                .padding(top = 100.dp)
                .size(96.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(R.string.favorites_empty),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 19.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
