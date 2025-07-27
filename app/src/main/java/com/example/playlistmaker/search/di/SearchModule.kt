package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val searchModule = module {
    single<TracksRepository> { TracksRepositoryImpl(get()) }
    factory<TracksInteractor> { TracksInteractorImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = get(),
            iTunesService = get()
        )
    }

    single<StorageClient<ArrayList<Track>>> {
        PrefsStorageClient(
            context = get(),
            dataKey = "SEARCH_HISTORY_KEY",
            type = object : TypeToken<ArrayList<Track>>() {}.type
        )
    }

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            searchHistoryInteractor = get()
        )
    }
}
