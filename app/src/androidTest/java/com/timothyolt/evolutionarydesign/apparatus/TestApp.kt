package com.timothyolt.evolutionarydesign.apparatus

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.timothyolt.evolutionarydesign.EvolutionaryDesignApp
import com.timothyolt.evolutionarydesign.Injector
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2
import kotlin.reflect.jvm.javaMethod

class TestApp : Application(), EvolutionaryDesignApp {

    companion object {
        fun require() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
    }

    override val injector = Proxy.newProxyInstance(
        Injector::class.java.classLoader,
        arrayOf(Injector::class.java)
    ) proxyMethod@{ _, method, args ->
        val injector = injectorsByMethod[method]
        if (injector != null) {
            injector.invoke(args.first())
        } else {
            error("No Injector registered for $method")
        }
    } as Injector

    private val injectorsByMethod: MutableMap<Method, Function1<Any, Any?>> = mutableMapOf()

    fun <T : Any, R> registerInjector(
        injectMethod: KFunction2<Injector, T, R>,
        inject: (T) -> R
    ) {
        val javaMethod = injectMethod.legalJavaMethod
        if (javaMethod !in injectorsByMethod) {
            @Suppress("UNCHECKED_CAST")
            // by capturing the inject method, we should be able to guard this unchecked cast at compile time
            injectorsByMethod[javaMethod] = inject as Function1<Any, Any?>
        } else error("Already have Injector for $injectMethod!")
    }

    fun <T : Any, R> clearInjector(injectMethod: KFunction2<Injector, T, R>) {
        injectorsByMethod.remove(injectMethod.legalJavaMethod)
    }

    fun <T : Any, R, U> useInjector(
        injectMethod: KFunction2<Injector, T, R>,
        inject: (T) -> R,
        use: () -> U
    ): U {
        registerInjector(injectMethod, inject)
        val used = use()
        clearInjector(injectMethod)
        return used
    }

    private val KFunction<*>.legalJavaMethod get() = javaMethod ?: error("No constructors allowed!")

}

inline fun <reified A: Any, R> TestApp.registerInjector(
    injectMethod: KFunction2<Injector, A, R>,
    @Suppress("UNUSED_PARAMETER")
    // convenience to not specify Target AND Result type parameters
    targetClass: KClass<A> = A::class,
    noinline inject: A.() -> R
) = registerInjector(injectMethod, inject)

inline fun <reified A: Any, R> TestApp.clearInjector(
    injectMethod: KFunction2<Injector, A, R>,
    @Suppress("UNUSED_PARAMETER")
    // convenience to not specify Target AND Result type parameters
    targetClass: KClass<A> = A::class
) = clearInjector(injectMethod)

inline fun <reified A: Any, R, U> TestApp.useInjector(
    injectMethod: KFunction2<Injector, A, R>,
    @Suppress("UNUSED_PARAMETER")
    // convenience to not specify Target AND Result type parameters
    targetClass: KClass<A> = A::class,
    noinline inject: A.() -> R,
    noinline use: () -> U
): U = useInjector(injectMethod, inject, use)
