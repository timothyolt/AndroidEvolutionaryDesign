package com.timothyolt.evolutionarydesign

import android.app.Application
import android.content.Context

fun Context.injector() = (applicationContext as InjectingApplication).injector

interface InjectingApplication {
    val injector: Injector
}

class MyApplication : Application(), InjectingApplication {
    override val injector = MainInjector()
}
