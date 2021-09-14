package com.timothyolt.evolutionarydesign

import android.app.Activity
import android.os.Bundle

class AlbumActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            id = R.id.helloText
            text = "Hello World!"
        })
    }
}
