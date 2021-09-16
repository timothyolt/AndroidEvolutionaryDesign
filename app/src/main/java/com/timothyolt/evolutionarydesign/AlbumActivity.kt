package com.timothyolt.evolutionarydesign

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val viewModel: AlbumViewModel
    }

    private lateinit var dependencies: Dependencies
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = injector().inject(this)
        setContentView(R.layout.activity_album)
        
        val adapter = AlbumAdapter()
        findViewById<RecyclerView>(R.id.albumImagesRecycler).adapter = adapter 
        
        lifecycleScope.launch {
            dependencies.viewModel.album.collect { album ->
                findViewById<TextView>(R.id.albumTitle).text = album.title
                adapter.updateAlbum(album)
            }
        }
    }

}
