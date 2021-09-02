package com.timothyolt.evolutionarydesign.apparatus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.launchActivity
import com.timothyolt.evolutionarydesign.Injector
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

inline fun <reified A: Activity, R> launchActivity(
    injectMethod: KFunction2<Injector, A, R>,
    activityClass: KClass<A> = A::class,
    noinline init: (A) -> R
) = launchActivity(LaunchOptions(), injectMethod, activityClass, init)

data class LaunchOptions(
    val intent: Intent? = null,
    val activityOptions: Bundle? = null
)

inline fun <reified A: Activity, R> launchActivity(
    launchOptions: LaunchOptions = LaunchOptions(),
    injectMethod: KFunction2<Injector, A, R>,
    @Suppress("UNUSED_PARAMETER")
    // convenience to not specify Target AND Result type parameters
    activityClass: KClass<A> = A::class,
    noinline init: (A) -> R
) = TestApp.require().useInjector(injectMethod, init) {
    launchActivity<A>(launchOptions.intent, launchOptions.activityOptions)
}