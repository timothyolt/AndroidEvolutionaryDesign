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

fun String.asUrl(): URL {
    return URL(this)
}

suspend fun <T> URL.connection(block: suspend URLConnection.() -> T): T {
    return (openConnection() as HttpURLConnection).block()
}