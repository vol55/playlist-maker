package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeInteractor = Creator.provideThemeInteractor(this)
        settingsViewModel = ViewModelProvider(
            this, SettingsViewModel.getFactory(themeInteractor)
        )[SettingsViewModel::class.java]

        findViewById<MaterialToolbar>(R.id.settings_activity_toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.smThemeSwitcher)
        val shareOption = findViewById<MaterialTextView>(R.id.share_list_option)
        val contactOption = findViewById<MaterialTextView>(R.id.contact_support_list_option)
        val termsOption = findViewById<MaterialTextView>(R.id.terms_of_use_list_option)

        settingsViewModel.observeScreenState().observe(this) { state ->
            themeSwitcher.isChecked = state.isDarkTheme
        }

        settingsViewModel.observeShareIntent().observe(this) { intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.share_url)))
        }

        settingsViewModel.observeContactIntent().observe(this) { intent ->
            startActivity(intent)
        }

        settingsViewModel.observeOpenLink().observe(this) { uri ->
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.onThemeSwitched(isChecked)
        }

        shareOption.setOnClickListener {
            settingsViewModel.onShareClicked(getString(R.string.share_url))
        }

        contactOption.setOnClickListener {
            settingsViewModel.onContactClicked(
                email = getString(R.string.my_email),
                subject = getString(R.string.message_subject),
                message = getString(R.string.message)
            )
        }

        termsOption.setOnClickListener {
            settingsViewModel.onTermsClicked(getString(R.string.terms_of_use_url))
        }
    }
}
