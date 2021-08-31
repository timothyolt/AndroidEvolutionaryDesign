package com.timothyolt.evolutionarydesign.album

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.image.Image
import com.timothyolt.evolutionarydesign.image.ImageAdapter
import com.timothyolt.evolutionarydesign.R
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class AlbumActivity : AppCompatActivity() {

    data class Dependencies(val albumId: String)

    private lateinit var dependencies: Dependencies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = requireInjector().inject(this)
        setContentView(R.layout.activity_main)
        val adapter = ImageAdapter()
        findViewById<RecyclerView>(R.id.images).adapter = adapter

        lifecycleScope.launch {
            val album = getAlbum(albumId = dependencies.albumId)

            findViewById<TextView>(R.id.albumTitle).text = album.title
            adapter.updateImages(album.images)
        }
    }

    private suspend fun getAlbum(albumId: String): Album = withContext(Dispatchers.IO) {
        val connection = URL("https://api.imgur.com/3/album/$albumId")
            .let { it.openConnection() as HttpURLConnection }
            .apply {
                requestMethod = "GET"
                addRequestProperty("Authorization", "Client-ID 6b1112a4f9783ad")
            }

        val inputStream = BufferedInputStream(connection.inputStream)

        val bytes = inputStream.use { it.readBytes() }
        val string = String(bytes)
        val json = JSONObject(string)

        if (json.getBoolean("success")) {
            json.getJSONObject("data").run {
                Album(
                    title = getString("title"),
                    description = getString("description"),
                    images = getJSONArray("images").run {
                        (0 until length()).map {
                            getJSONObject(it).run {
                                Image(
                                    title = getString("title"),
                                    description = getString("description"),
                                    link = getString("link")
                                )
                            }
                        }
                    }
                )
            }
        } else error("http code ${json.getInt("status")}")
    }
}
