package com.timothyolt.evolutionarydesign.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.timothyolt.evolutionarydesign.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.runner.RunWith
import kotlin.test.*


@RunWith(AndroidJUnit4::class)
class AuthenticationInstrumentedTest {

    @Test
    fun startAuthentication() {
        Intents.init()
        val imgurUrl = "https://api.imgur.com/oauth2/authorize?client_id=6b1112a4f9783ad&response_type=token"
        val openImgurIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(imgurUrl))

        launchActivity<AuthenticationActivity>()

        intended(openImgurIntent)
        Intents.release()
    }

    @Test
    fun resumeAuthentication() {
        val mainActivityMonitor = getInstrumentation().addMonitor(MainActivity::class.java.name, null, false)
        val scenario = launchActivity<Activity>(
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse("http://timothyolt.com/android-evolutionary-design/login#access_token=test")
            }
        )
        // todo: assert login state set
        val mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5000)
        assertNotNull(mainActivity)
        mainActivity.finish()
        scenario.onActivity { assertEquals(true, it.isFinishing) }
    }
}