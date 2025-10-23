package com.example.playlistmaker.library.data.db.di

import androidx.room.Room
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.PlaylistDao
import com.example.playlistmaker.library.data.db.PlaylistTracksDao
import com.example.playlistmaker.library.data.db.TrackDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "database.db"
        ).build()
    }

    single<TrackDao> { get<AppDatabase>().trackDao() }
    single<PlaylistDao> { get<AppDatabase>().playlistDao() }
    single<PlaylistTracksDao> { get<AppDatabase>().playlistTracksDao() }
}
