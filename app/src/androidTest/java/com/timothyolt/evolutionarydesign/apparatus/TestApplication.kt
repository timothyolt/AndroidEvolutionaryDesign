package com.timothyolt.evolutionarydesign.apparatus

import android.app.Application
import com.timothyolt.evolutionarydesign.AlbumActivity
import com.timothyolt.evolutionarydesign.AlbumService
import com.timothyolt.evolutionarydesign.InjectingApplication

class TestApplication : Application(), InjectingApplication {
    override fun inject(albumActivity: AlbumActivity) = object : AlbumActivity.Dependencies {
        override val albumService = object : AlbumService {
            override suspend fun getAlbumTitle() = "NotUDP"
        }
    }
}
