package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class NetworkAlbumRepositoryTest {
    @Test
    fun albumTitle() {
        val albumService = NetworkAlbumRepository()
        val album = runBlocking {
            albumService.getAlbum("kXieZOB")
        }
        assertEquals("Udp", album.title)
    }

    @Test
    fun albumImage() {
        val albumService = NetworkAlbumRepository()
        val album = runBlocking {
            albumService.getAlbum("kXieZOB")
        }
        val imageBytes = this::class.java.classLoader!!
            .getResourceAsStream("kXieZOB.png")!!.readBytes()
        val image = Image(imageBytes)
        assertEquals(image, album.images.first())
    }
}
