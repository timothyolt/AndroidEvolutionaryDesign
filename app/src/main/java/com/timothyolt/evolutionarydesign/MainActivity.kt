package com.timothyolt.evolutionarydesign

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    data class Dependencies(val albumId: String)

    private lateinit var dependencies: Dependencies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = requireInjector().inject(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val album = getAlbum(albumId = dependencies.albumId)
            val image = album.images.first()
            val bitmap = getBitmap(image.link)

            findViewById<TextView>(R.id.hello).text = image.title
            findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
        }
    }

    data class Album(
        val title: String,
        val description: String,
        val images: List<Image>
    ) {
        data class Image(
            val title: String,
            val description: String,
            val link: String
        )
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
                                Album.Image(
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

    private suspend fun getBitmap(imageUrl: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(imageUrl)
            .let { it.openConnection() as HttpURLConnection }
            .apply { requestMethod = "GET" }

        val inputStream = BufferedInputStream(connection.inputStream)

        val data = inputStream.use { inputStream.readBytes() }

        connection.disconnect()

        BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
