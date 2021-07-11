package com.timothyolt.evolutionarydesign.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AuthenticationInstrumentedTest {
    @Test
    fun startAuthentication() {
        val activity = launchActivity<Activity>(
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse("https://timothyolt.com/android-evolutionary-design/login")
            }
        )
        activity.moveToState(Lifecycle.State.DESTROYED)
    }
}