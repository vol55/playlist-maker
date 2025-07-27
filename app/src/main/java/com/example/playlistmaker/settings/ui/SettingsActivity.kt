package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialToolbar>(R.id.settings_activity_toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.smThemeSwitcher)
        val shareOption = findViewById<MaterialTextView>(R.id.share_list_option)
        val contactOption = findViewById<MaterialTextView>(R.id.contact_support_list_option)
        val termsOption = findViewById<MaterialTextView>(R.id.terms_of_use_list_option)

        viewModel.observeScreenState().observe(this) { state ->
            themeSwitcher.isChecked = state.isDarkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }

        viewModel.observeShareIntent().observe(this) { intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.share_url)))
        }

        viewModel.observeContactIntent().observe(this) { intent ->
            startActivity(intent)
        }

        viewModel.observeOpenLink().observe(this) { uri ->
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }

        shareOption.setOnClickListener {
            viewModel.onShareClicked(getString(R.string.share_url))
        }

        contactOption.setOnClickListener {
            viewModel.onContactClicked(
                email = getString(R.string.my_email),
                subject = getString(R.string.message_subject),
                message = getString(R.string.message)
            )
        }

        termsOption.setOnClickListener {
            viewModel.onTermsClicked(getString(R.string.terms_of_use_url))
        }
    }
}
