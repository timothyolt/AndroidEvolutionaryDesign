package com.timothyolt.evolutionarydesign.apparatus.idling

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.job
import java.util.*

class LifecycleCoroutineIdlingResource private constructor(
    private val counter: CountingIdlingResource
) : IdlingResource by counter {

    constructor(
        name: String = "lifecycleCoroutines-${UUID.randomUUID()}"
    ) : this(CountingIdlingResource(name))

    fun idleUntilCurrentJobsFinish(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.coroutineContext.job
            .children
            .forEach { child ->
                counter.increment()
                child.invokeOnCompletion {
                    counter.decrement()
                }
            }
    }
}
