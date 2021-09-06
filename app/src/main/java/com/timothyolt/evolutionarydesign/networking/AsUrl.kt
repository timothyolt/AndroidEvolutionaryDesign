package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun String.asUrl() = withContext(Dispatchers.IO) {
    URL(this@asUrl)
}
