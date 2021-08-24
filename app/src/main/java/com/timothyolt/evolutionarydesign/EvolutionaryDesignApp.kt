package com.timothyolt.evolutionarydesign

import android.app.Application
import android.content.Context
import com.timothyolt.evolutionarydesign.auth.AuthenticationActivity

interface EvolutionaryDesignApp {
    val injector: Injector
}

interface Injector {
    fun injectMain(mainActivity: MainActivity): MainActivity.Dependencies
    fun injectAuth(mainActivity: AuthenticationActivity): String
}

fun Context.requireInjector() = (applicationContext as EvolutionaryDesignApp).injector

fun MainActivity.injectMe() = requireInjector().injectMain(this)

class ReleaseEvolutionaryDesignApp : Application(), EvolutionaryDesignApp {

    override val injector = object : Injector {
        override fun injectMain(mainActivity: MainActivity) =
            MainActivity.Dependencies(httpUrl = "https://i.imgur.com/GSwTJrM.jpeg")

        override fun injectAuth(mainActivity: AuthenticationActivity) = ""
    }

}