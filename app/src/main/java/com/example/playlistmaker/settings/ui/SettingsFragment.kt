package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.MyAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyAppTheme {
                    val state by viewModel.observeScreenState().observeAsState(SettingsState())

                    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner) {
                        val shareObserver = Observer<Intent> { intent ->
                            startActivity(
                                Intent.createChooser(intent, getString(R.string.share_url))
                            )
                        }
                        val contactObserver = Observer<Intent> { intent ->
                            startActivity(intent)
                        }
                        val linkObserver = Observer<android.net.Uri> { uri ->
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }

                        viewModel.observeShareIntent().observe(lifecycleOwner, shareObserver)
                        viewModel.observeContactIntent().observe(lifecycleOwner, contactObserver)
                        viewModel.observeOpenLink().observe(lifecycleOwner, linkObserver)

                        onDispose {
                            viewModel.observeShareIntent().removeObserver(shareObserver)
                            viewModel.observeContactIntent().removeObserver(contactObserver)
                            viewModel.observeOpenLink().removeObserver(linkObserver)
                        }
                    }

                    SettingsScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        isDarkThemeEnabled = state.isDarkTheme,
                        onDarkThemeEnabledChange = viewModel::onThemeSwitched,
                        onShareAppClick = {
                            viewModel.onShareClicked(getString(R.string.share_url))
                        },
                        onContactSupportClick = {
                            viewModel.onContactClicked(
                                email = getString(R.string.my_email),
                                subject = getString(R.string.message_subject),
                                message = getString(R.string.message)
                            )
                        },
                        onTermsOfUseClick = {
                            viewModel.onTermsClicked(getString(R.string.terms_of_use_url))
                        })
                }
            }
        }
    }
}
