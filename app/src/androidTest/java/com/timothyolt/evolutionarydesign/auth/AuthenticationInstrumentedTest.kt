package com.timothyolt.evolutionarydesign.auth

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.timothyolt.evolutionarydesign.*
import com.timothyolt.evolutionarydesign.album.AlbumActivity
import com.timothyolt.evolutionarydesign.apparatus.LaunchOptions
import com.timothyolt.evolutionarydesign.apparatus.StubActivity
import com.timothyolt.evolutionarydesign.apparatus.launchActivity
import org.hamcrest.Matchers.allOf
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AuthenticationInstrumentedTest {

    @Test
    fun startAuthentication() {
        Intents.init()
        val openImgurIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData("http://timothyolt.com"))
        intending(openImgurIntent)

        val authentication = Authentication()
        val initialAuthenticationState = authentication.state

        launchActivity(Injector::inject, AuthenticationActivity::class) { activity ->
            object : AuthenticationActivity.Dependencies {
                override val oAuthRequestUrl = "http://timothyolt.com"
                override val oAuthCallbackUrl = "http://timothyolt.com/android-evolutionary-design/login"
                override val navigateToMain = Intent(activity, AlbumActivity::class.java)
                override val authentication = authentication
            }
        }

        assertEquals(initialAuthenticationState, authentication.state)
        intended(openImgurIntent)
        Intents.release()
    }

    @Test
    fun resumeAuthentication() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val mainActivityMonitor = instrumentation.addMonitor(StubActivity::class.java.name, null, false)

        val authentication = Authentication()

        val intent = Intent(
            Intent.ACTION_VIEW
        ).apply {
            data = Uri.parse("http://timothyolt.com/android-evolutionary-design/login#access_token=test")
        }
        val scenario = launchActivity(LaunchOptions(intent), Injector::inject, AuthenticationActivity::class) { activity ->
            object : AuthenticationActivity.Dependencies {
                override val oAuthRequestUrl = "http://timothyolt.com"
                override val oAuthCallbackUrl = "http://timothyolt.com/android-evolutionary-design/login"
                override val navigateToMain = Intent(instrumentation.context, StubActivity::class.java)
                override val authentication = authentication
            }
        }

        assertEquals(
            Authentication.State(
                accessToken = "test",
                tokenType = null,
                expiresIn = null
            ),
            authentication.state
        )
        val hitMainActivity = instrumentation.checkMonitorHit(mainActivityMonitor, 1)
        assertEquals(true, hitMainActivity)

        if (scenario.state != Lifecycle.State.DESTROYED) {
            scenario.onActivity { assertEquals(true, it?.isFinishing) }
        }

        instrumentation.removeMonitor(mainActivityMonitor)
    }
}
