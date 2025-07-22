package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.util.SingleLiveEvent

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    companion object {
        fun getFactory(themeInteractor: ThemeInteractor): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(themeInteractor)
                }
            }
    }

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
