package com.timothyolt.evolutionarydesign

import android.app.Application

fun inject(albumActivity: AlbumActivity) =
    (albumActivity.applicationContext as InjectingApplication).inject(albumActivity)

interface InjectingApplication {
    fun inject(albumActivity: AlbumActivity): AlbumActivity.Dependencies
}

class MyApplication : Application(), InjectingApplication {

    private val applicationAlbumService = NetworkAlbumService()

    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val albumService: AlbumService = applicationAlbumService
    }
}
