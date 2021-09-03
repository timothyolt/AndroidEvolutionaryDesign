package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLConnection

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun URLConnection.readBytes(): ByteArray = withContext(Dispatchers.IO) {
    getInputStream()
        .buffered()
        .use { it.readBytes() }
}
