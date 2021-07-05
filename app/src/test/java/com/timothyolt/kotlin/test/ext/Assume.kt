package com.timothyolt.kotlin.test.ext

import junit.framework.AssertionFailedError
import org.junit.AssumptionViolatedException
import java.lang.AssertionError

fun <T> assume(block: () -> T): T {
    return try {
        block()
    } catch (testError: AssertionFailedError) {
        throw AssumptionViolatedException(testError.message, testError)
    } catch (testError: AssertionError) {
        throw AssumptionViolatedException(testError.message, testError)
    }
}