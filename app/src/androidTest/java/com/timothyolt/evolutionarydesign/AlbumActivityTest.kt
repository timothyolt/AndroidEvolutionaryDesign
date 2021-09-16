package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.timothyolt.evolutionarydesign.apparatus.idling.LifecycleCoroutineIdlingResource
import com.timothyolt.evolutionarydesign.apparatus.idling.registerLifecycleIdling
import com.timothyolt.evolutionarydesign.apparatus.idling.unregisterLifecycleIdling
import com.timothyolt.evolutionarydesign.apparatus.launchActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AlbumActivityTest {

    @Test
    fun albumTitleRenders() {
        val idler = LifecycleCoroutineIdlingResource()
        registerLifecycleIdling(idler)

        launchActivity(Injector::inject, AlbumActivity::class) {
            object : AlbumActivity.Dependencies {
                override val albumService = object : AlbumService {
                    override suspend fun getAlbum() = AlbumService.Album(
                        title = "NotUDP",
                        images = listOf(Image(ByteArray(0)))
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

    @Test
    fun albumFirstImageRenders() {
        val idler = LifecycleCoroutineIdlingResource()
        registerLifecycleIdling(idler)

        val bytes1by1 = getInstrumentation().context
            .resources.assets.open("1by1.png").readBytes()
        val bitmapOneByOne = BitmapFactory.decodeByteArray(bytes1by1, 0, bytes1by1.size)

        launchActivity(Injector::inject, AlbumActivity::class) {
            object : AlbumActivity.Dependencies {
                override val albumService = object : AlbumService {
                    override suspend fun getAlbum() = AlbumService.Album(
                        title = "title",
                        images = listOf(Image(bytes1by1))
                    )
                }
            }
        }.onActivity { activity ->
            idler.idleUntilCurrentJobsFinish(activity)
        }

        onView(
            allOf(
                withParent(withId(R.id.albumImagesRecycler)),
                withParentIndex(0)
            )
        ).check { view, _ ->
            val imageView = view.findViewById<ImageView>(R.id.image)
            val bitmapDrawable = imageView.drawable as BitmapDrawable

            // probably incorrect
            assertTrue(bitmapOneByOne.sameAs(bitmapDrawable.bitmap))
        }

        unregisterLifecycleIdling(idler)
    }
}
