package com.timothyolt.evolutionarydesign

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.timothyolt.evolutionarydesign.auth.AuthenticationActivity
import org.junit.runner.RunWith
import kotlin.test.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.timothyolt.evolutionarydesign", appContext.packageName)
    }

    @Test
    fun test() {
        TestApp.require().useInjector(Injector::injectAuth, { " " }) {
            launchActivity(Injector::injectMain) {
                MainActivity.Dependencies("https://static.wikia.nocookie.net/starwars/images/6/67/DarkWolf-LoNH.jpg")
            }
        }

        launchActivity(Injector::injectMain) {
            MainActivity.Dependencies("https://static.wikia.nocookie.net/starwars/images/6/67/DarkWolf-LoNH.jpg")
        }
    }
}
