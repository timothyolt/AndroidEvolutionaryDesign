package com.timothyolt.evolutionarydesign

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val albumService: AlbumService
    }

    private lateinit var dependencies: Dependencies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = inject(this)
        setContentView(R.layout.activity_album)
        lifecycleScope.launch {
            findViewById<TextView>(R.id.helloText).text = dependencies.albumService.getAlbumTitle()
        }
    }

}
