package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.util.SingleLiveEvent

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData(
        SettingsState(isDarkTheme = themeInteractor.isDarkTheme())
    )

    fun observeScreenState(): LiveData<SettingsState> = screenStateLiveData

    private val shareIntentLiveData = SingleLiveEvent<Intent>()
    fun observeShareIntent(): LiveData<Intent> = shareIntentLiveData

    private val contactIntentLiveData = SingleLiveEvent<Intent>()
    fun observeContactIntent(): LiveData<Intent> = contactIntentLiveData

    private val openLinkLiveData = SingleLiveEvent<Uri>()
    fun observeOpenLink(): LiveData<Uri> = openLinkLiveData

    fun onThemeSwitched(isDark: Boolean) {
        themeInteractor.switchTheme(isDark)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        screenStateLiveData.value = screenStateLiveData.value?.copy(isDarkTheme = isDark)
    }

    fun onShareClicked(shareText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        shareIntentLiveData.postValue(intent)
    }

    fun onContactClicked(email: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        contactIntentLiveData.postValue(intent)
    }

    fun onTermsClicked(url: String) {
        openLinkLiveData.postValue(url.toUri())
    }
}
