package com.timothyolt.evolutionarydesign

interface Injector {
    fun inject(albumActivity: AlbumActivity): AlbumActivity.Dependencies
}

class MainInjector : Injector {
    private val applicationAlbumRepository = NetworkAlbumRepository()

    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val albumRepository: AlbumRepository = applicationAlbumRepository
    }
}
