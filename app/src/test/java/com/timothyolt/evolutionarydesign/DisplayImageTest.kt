package com.timothyolt.evolutionarydesign

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import kotlin.test.*

class DisplayImageTest {
    @Test
    fun test() {
        val shapeDrawable = ShapeDrawable()
        val presenter = Presenter(shapeDrawable)
        assertEquals(shapeDrawable, presenter.image)
    }
}

class Presenter(
    private val empty: Drawable
) {
    val image: Drawable = empty
}
