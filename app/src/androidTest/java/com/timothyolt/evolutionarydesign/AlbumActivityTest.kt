package com.timothyolt.evolutionarydesign

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.timothyolt.evolutionarydesign.apparatus.idling.LifecycleCoroutineIdlingResource
import com.timothyolt.evolutionarydesign.apparatus.idling.registerLifecycleIdling
import com.timothyolt.evolutionarydesign.apparatus.idling.unregisterLifecycleIdling
import com.timothyolt.evolutionarydesign.apparatus.launchActivity
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AlbumActivityTest {

    @Test
    fun test() {
        val idler = LifecycleCoroutineIdlingResource()
        registerLifecycleIdling(idler)

        launchActivity(Injector::inject, AlbumActivity::class) {
            object : AlbumActivity.Dependencies {
                override val albumService = object : AlbumService {
                    override suspend fun getAlbum() = AlbumService.Album(
                        title = "NotUDP",
                        image = Image(ByteArray(0))
                    )
                }
            }
        }.onActivity { activity ->
            idler.idleUntilCurrentJobsFinish(activity)
        }

        onView(withId(R.id.imageTitle))
            .check(matches(withText("NotUDP")))

        unregisterLifecycleIdling(idler)
    }
}
