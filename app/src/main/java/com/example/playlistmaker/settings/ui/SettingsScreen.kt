package com.example.playlistmaker.settings.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.appColors

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    isDarkThemeEnabled: Boolean,
    onDarkThemeEnabledChange: (Boolean) -> Unit,
    onShareAppClick: () -> Unit = {},
    onContactSupportClick: () -> Unit = {},
    onTermsOfUseClick: () -> Unit = {},
) {
    val items = listOf(
        SettingsMenuItem(
            title = stringResource(R.string.share_app),
            iconRes = R.drawable.share,
            onClick = onShareAppClick
        ),
        SettingsMenuItem(
            title = stringResource(R.string.contact_support),
            iconRes = R.drawable.support,
            onClick = onContactSupportClick
        ),
        SettingsMenuItem(
            title = stringResource(R.string.terms_of_use),
            iconRes = R.drawable.arrow_forward,
            onClick = onTermsOfUseClick
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        ThemeToggleRow(
            title = stringResource(R.string.dark_theme),
            checked = isDarkThemeEnabled,
            onCheckedChange = onDarkThemeEnabledChange
        )

        items.forEach { item ->
            NavigationRow(
                title = item.title, iconRes = item.iconRes, onClick = item.onClick
            )
        }
    }
}

@Composable
private fun ThemeToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsListRow(
        title = title, modifier = modifier, onClick = null, trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.appColors.ysBlue,
                    checkedTrackColor = MaterialTheme.appColors.themeSwitchTrackColor,
                    uncheckedThumbColor = MaterialTheme.appColors.ysGray,
                    uncheckedTrackColor = MaterialTheme.appColors.themeSwitchTrackColor,

                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent,
                    disabledCheckedBorderColor = Color.Transparent,
                    disabledUncheckedBorderColor = Color.Transparent,
                )
            )

        })
}

@Composable
private fun NavigationRow(
    title: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsListRow(
        title = title, modifier = modifier, onClick = onClick, trailing = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.appColors.ysGray,
                modifier = Modifier.padding(end = 8.dp)
            )
        })
}

@Composable
private fun SettingsListRow(
    title: String,
    trailing: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        trailing?.invoke()
    }
}

private data class SettingsMenuItem(
    val title: String,
    @param:DrawableRes val iconRes: Int,
    val onClick: () -> Unit,
)
