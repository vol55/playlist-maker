package com.example.playlistmaker.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.appColors
import com.example.playlistmaker.search.domain.models.Track

@Composable
fun TrackLazyColumn(
    tracks: List<Track>, onTrackClick: (Track) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier, contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(
            items = tracks, key = { it.trackId }) { track ->
            TrackItem(
                track = track, modifier = Modifier.clickable { onTrackClick(track) })
        }
    }
}

@Composable
private fun TrackItem(
    track: Track,
    modifier: Modifier = Modifier,
    imageSize: Dp = 48.dp,
    horizontalMargin: Dp = 16.dp,
    verticalMargin: Dp = 8.dp,
    space8: Dp = 8.dp,
    space16: Dp = 16.dp,
) {
    val description = "${track.artistName} • ${track.trackDuration}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalMargin, vertical = verticalMargin)
            .height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically
    ) {
        Cover(
            url = track.coverArtworkUrl, modifier = Modifier.size(imageSize), cornerRadius = 2.dp
        )

        Column(
            modifier = Modifier
                .padding(start = space8, end = space16)
                .weight(1f)
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = description,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.appColors.foregroundVariant,
            )
        }

        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = stringResource(R.string.forward),
            tint = MaterialTheme.appColors.foregroundVariant,
            modifier = Modifier
                .fillMaxHeight()
                .width(space8)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Cover(
    url: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 2.dp,
) {
    val shape = RoundedCornerShape(cornerRadius)

    GlideImage(
        model = url,
        contentDescription = stringResource(R.string.cover),
        modifier = modifier.clip(shape),
        contentScale = ContentScale.Crop
    ) { request ->
        request.placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
    }
}
