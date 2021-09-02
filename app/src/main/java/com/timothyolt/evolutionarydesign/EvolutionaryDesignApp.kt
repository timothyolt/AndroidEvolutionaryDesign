package com.timothyolt.evolutionarydesign

import android.app.Application
import android.content.Context
import android.content.Intent
import com.timothyolt.evolutionarydesign.album.AlbumActivity
import com.timothyolt.evolutionarydesign.auth.AuthenticationActivity

interface EvolutionaryDesignApp {
    val injector: Injector
}

interface Injector {
    fun inject(activity: AlbumActivity): AlbumActivity.Dependencies
    fun inject(activity: AuthenticationActivity): AuthenticationActivity.Dependencies
}

fun Context.requireInjector() = (applicationContext as EvolutionaryDesignApp).injector

class ReleaseEvolutionaryDesignApp : Application(), EvolutionaryDesignApp {

    override val injector = object : Injector {
        override fun inject(activity: AlbumActivity) =
            AlbumActivity.Dependencies(albumId = "dTI1d")

        override fun inject(activity: AuthenticationActivity) =
            object : AuthenticationActivity.Dependencies {
                override val oAuthRequestUrl = "https://api.imgur.com/oauth2/authorize?client_id=6b1112a4f9783ad&response_type=token"
                override val oAuthCallbackUrl = "http://timothyolt.com/android-evolutionary-design/login"
                override val navigateToMain = Intent(activity, AlbumActivity::class.java)
            }
    }

}
