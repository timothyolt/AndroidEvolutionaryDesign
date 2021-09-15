package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
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
        dependencies = injector().inject(this)
        setContentView(R.layout.activity_album)
        lifecycleScope.launch {
            val album = dependencies.albumService.getAlbum()
            findViewById<TextView>(R.id.imageTitle).text = album.title
            val bitmap = album.image.toBitmap()
            findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
        }
    }

    private fun Image.toBitmap() =
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

}
