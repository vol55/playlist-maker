package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.EXTRA_EMAIL
import android.net.Uri
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<FrameLayout>(R.id.share_app_button)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(EXTRA_TEXT, R.string.share_url)
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }

        val contactButton = findViewById<FrameLayout>(R.id.contact_support_button)
        contactButton.setOnClickListener {
            val contactIntent = Intent(Intent.ACTION_SENDTO)
            contactIntent.data = "mailto:".toUri()
            contactIntent.putExtra(EXTRA_EMAIL, arrayOf(R.string.my_email))
            contactIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.message_subject)
            contactIntent.putExtra(EXTRA_TEXT, R.string.message)
            startActivity(contactIntent)
        }

        val termsOfUseButton = findViewById<FrameLayout>(R.id.terms_of_use_button)
        termsOfUseButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, getString(R.string.terms_of_use_url).toUri())
            startActivity(browserIntent)
        }
    }
}
