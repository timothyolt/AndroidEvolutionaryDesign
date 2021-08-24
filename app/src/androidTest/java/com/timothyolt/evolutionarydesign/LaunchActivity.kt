package com.timothyolt.evolutionarydesign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.launchActivity
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

inline fun <reified A: Activity, R> launchActivity(
    injectMethod: KFunction2<Injector, A, R>,
    activityClass: KClass<A> = A::class,
    noinline init: A.() -> R
) = launchActivity(null, null, injectMethod, activityClass, init)

inline fun <reified A: Activity, R> launchActivity(
    intent: Intent? = null,
    activityOptions: Bundle? = null,
    injectMethod: KFunction2<Injector, A, R>,
    @Suppress("UNUSED_PARAMETER")
    // convenience to not specify Target AND Result type parameters
    activityClass: KClass<A> = A::class,
    noinline init: A.() -> R
) {
    TestApp.require().useInjector(injectMethod, init) {
        launchActivity<A>(intent, activityOptions)
    }
}