package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun URLConnection.readBytes(): ByteArray = withContext(Dispatchers.IO) {
    getInputStream()
        .buffered()
        .use { it.readBytes() }
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun HttpURLConnection.readErrorBytes(): ByteArray = withContext(Dispatchers.IO) {
    errorStream
        .buffered()
        .use { it.readBytes() }
}

