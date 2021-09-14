package com.timothyolt.evolutionarydesign

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AlbumActivityTest {

    @Test
    fun test() {
        launchActivity<AlbumActivity>()
    }
}
