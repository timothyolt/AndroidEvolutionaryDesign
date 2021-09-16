package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class NetworkAlbumServiceTest {
    @Test
    fun albumTitle() {
        val albumService = NetworkAlbumService()
        val album = runBlocking {
            albumService.getAlbum()
        }
        assertEquals("Udp", album.title)
    }

    @Test
    fun albumImage() {
        val albumService = NetworkAlbumService()
        val album = runBlocking {
            albumService.getAlbum()
        }
        val imageBytes = this::class.java.classLoader!!
            .getResourceAsStream("kXieZOB.png")!!.readBytes()
        val image = Image(imageBytes)
        assertEquals(image, album.images)
    }
}
