package com.timothyolt.evolutionarydesign

import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.registerIdlingResources
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.job
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AlbumActivityTest {

    @Test
    fun test() {
        val resource = CountingIdlingResource("name")
        IdlingRegistry.getInstance().register(resource)
        launchActivity<AlbumActivity>().onActivity {
            it.lifecycleScope.coroutineContext.job
                .children
                .forEach { child ->
                    resource.increment()
                    child.invokeOnCompletion {
                        resource.decrement()
                    }
                }
        }
        onView(withId(R.id.helloText))
            .check(matches(withText("Udp")))
        IdlingRegistry.getInstance().unregister(resource)
    }
}
