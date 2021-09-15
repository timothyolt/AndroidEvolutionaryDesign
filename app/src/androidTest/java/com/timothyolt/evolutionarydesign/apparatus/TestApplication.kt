package com.timothyolt.evolutionarydesign.apparatus

import android.app.Application
import com.timothyolt.evolutionarydesign.AlbumActivity
import com.timothyolt.evolutionarydesign.AlbumService
import com.timothyolt.evolutionarydesign.InjectingApplication
import com.timothyolt.evolutionarydesign.Injector

class TestApplication : Application(), InjectingApplication {

    override val injector = object : Injector {
        override fun inject(albumActivity: AlbumActivity) =
            object : AlbumActivity.Dependencies {
                override val albumService = object : AlbumService {
                    override suspend fun getAlbumTitle() = "NotUDP"
                }
            }
    }
}
