package com.timothyolt.evolutionarydesign

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val albumRepository: AlbumRepository
    }

    private lateinit var dependencies: Dependencies
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = injector().inject(this)
        setContentView(R.layout.activity_album)
        
        val adapter = AlbumAdapter()
        findViewById<RecyclerView>(R.id.albumImagesRecycler).adapter = adapter 
        
        lifecycleScope.launch {
            val album = dependencies.albumRepository.getAlbum()

            findViewById<TextView>(R.id.imageTitle).text = album.title
            adapter.updateAlbum(album)
        }
    }

}
