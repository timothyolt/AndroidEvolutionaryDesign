package com.timothyolt.evolutionarydesign.apparatus.idling

import androidx.test.espresso.IdlingRegistry

fun registerLifecycleIdling(resource: LifecycleCoroutineIdlingResource) {
    IdlingRegistry.getInstance().register(resource)
}

fun unregisterLifecycleIdling(resource: LifecycleCoroutineIdlingResource) {
    IdlingRegistry.getInstance().unregister(resource)
}
