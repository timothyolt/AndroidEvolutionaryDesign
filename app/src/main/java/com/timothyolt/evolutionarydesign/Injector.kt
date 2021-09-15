package com.timothyolt.evolutionarydesign

interface Injector {
    fun inject(albumActivity: AlbumActivity): AlbumActivity.Dependencies
}

class MainInjector : Injector {
    private val applicationAlbumService = NetworkAlbumService()

    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val albumService: AlbumService = applicationAlbumService
    }
}
