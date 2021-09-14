package com.timothyolt.evolutionarydesign.album

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.R
import com.timothyolt.evolutionarydesign.image.ImageAdapter
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.timothyolt.evolutionarydesign.upload.UploadService

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val viewModel: AlbumViewModel
    }

    private lateinit var dependencies: Dependencies

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        startService(UploadService.createIntent(this, uri.toString()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dependencies = requireInjector().inject(this)
        setContentView(R.layout.activity_main)

        val adapter = ImageAdapter()
        findViewById<RecyclerView>(R.id.images).adapter = adapter

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            pickImage.launch("image/*")
        }

        lifecycleScope.launch {
            dependencies.viewModel.albumState.collect { album ->
                findViewById<TextView>(R.id.albumTitle).text = album.title

                adapter.updateImages(album.images)
            }
        }
    }
}
