package com.example.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.library.data.db.di.dataModule
import com.example.playlistmaker.library.di.libraryModule
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                playerModule,
                settingsModule,
                searchModule,
                libraryModule,
            )
        }

        val themeInteractor: ThemeInteractor = get(ThemeInteractor::class.java)
        applyTheme(themeInteractor.isDarkTheme())
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
