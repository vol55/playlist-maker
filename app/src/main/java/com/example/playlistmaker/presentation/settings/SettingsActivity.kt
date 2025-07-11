package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeInteractor: com.example.playlistmaker.domain.api.ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeInteractor = Creator.provideThemeInteractor(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.settings_activity_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.smThemeSwitcher)
        themeSwitcher.isChecked = themeInteractor.isDarkTheme()
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeInteractor.switchTheme(checked)
        }

        val shareListOption = findViewById<MaterialTextView>(R.id.share_list_option)
        shareListOption.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(EXTRA_TEXT, getString(R.string.share_url))
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }

        val contactButton = findViewById<MaterialTextView>(R.id.contact_support_list_option)
        contactButton.setOnClickListener {
            val contactIntent = Intent(Intent.ACTION_SENDTO)
            contactIntent.data = "mailto:".toUri()
            contactIntent.putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            contactIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
            contactIntent.putExtra(EXTRA_TEXT, getString(R.string.message))
            startActivity(contactIntent)
        }

        val termsOfUseButton = findViewById<MaterialTextView>(R.id.terms_of_use_list_option)
        termsOfUseButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, getString(R.string.terms_of_use_url).toUri())
            startActivity(browserIntent)
        }
    }
}
