package com.timothyolt.evolutionarydesign.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicInteger

class ProgressTracker {
    private val progressId = AtomicInteger(0)

    private val isActiveMutable = MutableStateFlow(false)
    val isActive: Flow<Boolean> = isActiveMutable

    fun onStart() {
        progressId.incrementAndGet()
        isActiveMutable.value = true
    }

    fun onStop() {
        val newProgressCount = progressId.decrementAndGet()
        isActiveMutable.value = newProgressCount > 0
    }

    suspend fun <R> inProgress(block: suspend CoroutineScope.() -> R): R {
        return coroutineScope {
            onStart()
            val job = async { block() }
            job.invokeOnCompletion { onStop() }
            job.await()
        }
    }
}
