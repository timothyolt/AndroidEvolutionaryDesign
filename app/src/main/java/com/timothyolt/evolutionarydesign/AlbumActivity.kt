package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val albumService: AlbumService
    }

    private lateinit var dependencies: Dependencies
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = injector().inject(this)
        setContentView(R.layout.activity_album)
        
        val adapter = AlbumAdapter()
        findViewById<RecyclerView>(R.id.albumImagesRecycler).adapter = adapter 
        
        lifecycleScope.launch {
            val album = dependencies.albumService.getAlbum()
            findViewById<TextView>(R.id.imageTitle).text = album.title
            
            adapter.loadAlbum(album)
        }
    }

}
