package com.example.playlistmaker.search.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.appColors
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.toImmutableList


@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    state: SearchScreenState?,
    onDone: () -> Unit,
    onRetry: () -> Unit,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier,
) {
    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.search),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            onClearClick = onClearQuery,
            onDone = onDone
        )

        when (state) {
            is SearchScreenState.Loading -> Loading()
            is SearchScreenState.NoResults -> NoResults()
            is SearchScreenState.NotConnected -> NoNetwork(
                onRetryClick = onRetry, modifier = Modifier.fillMaxSize()
            )

            is SearchScreenState.Results -> {
                TrackLazyColumn(
                    tracks = state.tracks.toImmutableList(),
                    onTrackClick = onTrackClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is SearchScreenState.History -> {
                if (state.history.isNotEmpty()) {
                    SearchHistory(
                        history = state.history,
                        onTrackClick = onTrackClick,
                        onClearHistory = onClearHistory,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            null -> Unit
        }
    }
}

@Composable
fun SearchHistory(
    history: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.search_history),
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 19.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        TrackLazyColumn(
            tracks = history.toImmutableList(),
            onTrackClick = onTrackClick,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Button(
            onClick = onClearHistory,
            modifier = Modifier.padding(bottom = 12.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)

            )
        }
    }
}

@Composable
fun NoNetwork(
    onRetryClick: () -> Unit, modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val imageRes = if (isDark) R.drawable.no_network_dark else R.drawable.no_network_light

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
            text = stringResource(R.string.no_network_message),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier.padding(top = 24.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(R.string.refresh),
                color = MaterialTheme.colorScheme.background,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun NoResults(
    modifier: Modifier = Modifier
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
                .size(120.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(R.string.no_results_message),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.appColors.ysBlue,
            modifier = Modifier.padding(bottom = 260.dp),
        )
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onDone: (() -> Unit)? = null,
) {
    val appColors = MaterialTheme.appColors

    val context = LocalContext.current
    val view = LocalView.current
    val focusManager = LocalFocusManager.current

    fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),

        textStyle = MaterialTheme.typography.bodyMedium.copy(color = appColors.ysBlack),

        placeholder = {
            Text(
                text = stringResource(R.string.search), color = appColors.onTextField
            )
        },

        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null,
                tint = appColors.onTextField
            )
        },

        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        painter = painterResource(R.drawable.clear_icon),
                        contentDescription = stringResource(R.string.clear_input),
                        tint = appColors.onTextField
                    )
                }
            }
        },

        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                hideKeyboard()
                onDone?.invoke()
            }),

        colors = TextFieldDefaults.colors(
            focusedContainerColor = appColors.textField,
            unfocusedContainerColor = appColors.textField,
            disabledContainerColor = appColors.textField,
            cursorColor = appColors.ysBlue,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}
