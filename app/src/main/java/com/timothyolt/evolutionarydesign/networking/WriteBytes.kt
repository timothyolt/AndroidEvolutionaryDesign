package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLConnection

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun URLConnection.writeBytes(
    contentType: String,
    bytes: ByteArray
) = withContext(Dispatchers.IO) {
    doOutput = true
    setRequestProperty("Content-Type", contentType)
    getOutputStream()
        .buffered()
        .use { it.write(bytes) }
}
