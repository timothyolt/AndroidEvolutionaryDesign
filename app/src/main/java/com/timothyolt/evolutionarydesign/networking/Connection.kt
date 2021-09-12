package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLConnection

@JvmName("connectionOfType")
@Suppress("BlockingMethodInNonBlockingContext", "UNCHECKED_CAST")
suspend fun <C : URLConnection, R> URL.connection(block: suspend C.() -> R): R = withContext(Dispatchers.IO) {
    openConnection()
        .run { (this as C).block() }
    // not calling disconnect to allow JVM to pool TCP connections
    // https://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun <R> URL.connection(block: suspend URLConnection.() -> R): R = connection<URLConnection, R>(block)
