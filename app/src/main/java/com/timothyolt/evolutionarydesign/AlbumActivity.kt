package com.timothyolt.evolutionarydesign

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class AlbumActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Hello World!"
        })
    }
}
