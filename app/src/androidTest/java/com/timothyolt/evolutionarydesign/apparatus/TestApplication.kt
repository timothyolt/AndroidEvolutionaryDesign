package com.timothyolt.evolutionarydesign.apparatus

import android.app.Application
import com.timothyolt.evolutionarydesign.AlbumActivity
import com.timothyolt.evolutionarydesign.AlbumService
import com.timothyolt.evolutionarydesign.InjectingApplication
import com.timothyolt.evolutionarydesign.Injector

class TestApplication : Application(), InjectingApplication {
    override lateinit var injector: Injector
}
