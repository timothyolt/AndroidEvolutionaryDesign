package com.timothyolt.evolutionarydesign.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.runner.RunWith
import kotlin.test.*


@RunWith(AndroidJUnit4::class)
class AuthenticationInstrumentedTest {
    @Test
    fun resumeAuthentication() {
        val scenario = launchActivity<Activity>(
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse("http://timothyolt.com/android-evolutionary-design/login#access_token=test")
            }
        )
        // todo: test toast?
//        scenario.onActivity { activity ->
//            onView(
//                withText("test")
//            ).inRoot(
//                withDecorView(not(activity.window.decorView))
//            ).check(
//                matches(isDisplayed())
//            )
//        }
        assertEquals(Lifecycle.State.DESTROYED, scenario.state)
        // todo: assert login state set
    }

    @Test
    fun startAuthentication() {
        val activity = launchActivity<AuthenticationActivity>()
        activity.moveToState(Lifecycle.State.DESTROYED)
    }
}