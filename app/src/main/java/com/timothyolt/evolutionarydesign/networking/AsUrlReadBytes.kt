package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLConnection

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun String.asUrlReadBytes(block: URLConnection.() -> Unit): ByteArray = withContext(Dispatchers.IO) {
    URL(this@asUrlReadBytes)
        .openConnection()
        .apply(block)
        .getInputStream()
        .buffered()
        .use { it.readBytes() }
    // not calling disconnect to allow JVM to pool TCP connections
    // https://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html
}