package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class NetworkAlbumServiceTest {
    @Test
    fun test() {
        val albumService = NetworkAlbumService()
        val albumTitle = runBlocking {
            albumService.getAlbumTitle()
        }
        assertEquals("Udp", albumTitle)
    }
}
