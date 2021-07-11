package com.timothyolt.evolutionarydesign.auth

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AuthenticationInstrumentedTest {
    @Test
    fun startAuthentication() {
        val activity = launchActivity<AuthenticationActivity>()
        activity.moveToState(Lifecycle.State.DESTROYED)
    }
}