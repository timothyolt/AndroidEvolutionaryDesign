package com.timothyolt.evolutionarydesign.album

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.timothyolt.evolutionarydesign.Injector
import com.timothyolt.evolutionarydesign.apparatus.launchActivity
import com.timothyolt.evolutionarydesign.image.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.runner.RunWith
import org.junit.Test
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AlbumActivityTest {
    private val albumViewModel = object : AlbumViewModel {
        var albumStateRetrievalCounter = 0
        override val albumState: Flow<List<Image>>
            get() {
                albumStateRetrievalCounter++
                return flowOf()
            }
    }

    @Test
    fun onCreate_subscribeToAlbumUpdates() {
        launchActivity(Injector::inject, AlbumActivity::class) {
            AlbumActivity.Dependencies(
                "",
                albumViewModel
            )
        }

        assertTrue(albumViewModel.albumStateRetrievalCounter > 0)
    }

    companion object {
        private fun stubAlbumViewModel() = object : AlbumViewModel {
            override val albumState: Flow<List<Image>>
                get() = TODO("Not yet implemented")
        }
    }
}