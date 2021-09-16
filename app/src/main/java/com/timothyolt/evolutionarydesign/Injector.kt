package com.timothyolt.evolutionarydesign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

interface Injector {
    fun inject(albumActivity: AlbumActivity): AlbumActivity.Dependencies
}

class MainInjector : Injector {
    private val albumRepository = NetworkAlbumRepository()

    private val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlbumViewModel(albumRepository) as T
        }
    }

    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val viewModel: AlbumViewModel get() =
            ViewModelProvider(albumActivity.viewModelStore, viewModelFactory).get(AlbumViewModel::class.java)
    }
}
