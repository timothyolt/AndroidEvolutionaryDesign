package com.timothyolt.evolutionarydesign

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.timothyolt.evolutionarydesign.album.*
import com.timothyolt.evolutionarydesign.auth.Authentication
import com.timothyolt.evolutionarydesign.auth.AuthenticationActivity
import com.timothyolt.evolutionarydesign.networking.asUrl
import com.timothyolt.evolutionarydesign.networking.connection
import com.timothyolt.evolutionarydesign.networking.readBytes
import com.timothyolt.evolutionarydesign.notifications.Notifications
import com.timothyolt.evolutionarydesign.upload.UploadService
import kotlinx.coroutines.*

interface EvolutionaryDesignApp {
    val injector: Injector
}

interface Injector {
    fun inject(activity: AlbumActivity): AlbumActivity.Dependencies
    fun inject(activity: AuthenticationActivity): AuthenticationActivity.Dependencies
    fun inject(uploadService: UploadService): UploadService.Dependencies
}

fun Context.requireInjector() = (applicationContext as EvolutionaryDesignApp).injector

class ReleaseEvolutionaryDesignApp : Application(), EvolutionaryDesignApp {

    private val lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val authentication = Authentication()

    private val albumRepository: AlbumRepository by lazy {
        AlbumRepositoryImpl(
            "dTI1d",
            ImgurAlbumSource()
        )
    }

    override val injector = object : Injector {

        override fun inject(activity: AlbumActivity) =
            object : AlbumActivity.Dependencies {
                override val viewModel = AlbumViewModelImpl(albumRepository)
            }

        override fun inject(activity: AuthenticationActivity) =
            object : AuthenticationActivity.Dependencies {
                override val oAuthRequestUrl = "https://api.imgur.com/oauth2/authorize?client_id=${BuildConfig.IMGUR_CLIENT_ID}&response_type=token"
                override val oAuthCallbackUrl = "${BuildConfig.OAUTH_SCHEME}://${BuildConfig.OAUTH_HOST}${BuildConfig.OAUTH_PATH}"
                override val navigateToMain = Intent(activity, AlbumActivity::class.java)
                override val authentication = this@ReleaseEvolutionaryDesignApp.authentication
            }

        override fun inject(uploadService: UploadService) = object : UploadService.Dependencies {
            override val notifications get() = Notifications(
                context = applicationContext,
                uploadChannelId = "uploads"
            )
            override val accessToken: String get() = authentication.state!!.accessToken
            override val uploadNotificationId: Int = 1
        }
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            "https://api.imgur.com/3/credits".asUrl().connection {
                addRequestProperty("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
                Log.w("EvolutionaryDesignApp", String(readBytes()))
            }
        }
    }

}
