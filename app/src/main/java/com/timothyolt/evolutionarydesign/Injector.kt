package com.timothyolt.evolutionarydesign

interface Injector {
    fun inject(albumActivity: AlbumActivity): AlbumActivity.Dependencies
}

class MainInjector : Injector {
    private val albumRepository = NetworkAlbumRepository()

    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val viewModel: AlbumViewModel by lazy { AlbumViewModel(albumRepository) }
    }
}
