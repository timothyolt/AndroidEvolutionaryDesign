package com.timothyolt.evolutionarydesign.album

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.image.Image
import com.timothyolt.evolutionarydesign.image.ImageAdapter
import com.timothyolt.evolutionarydesign.R
import com.timothyolt.evolutionarydesign.auth.Authentication
import com.timothyolt.evolutionarydesign.networking.asUrlReadBytes
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val albumId: String
        val authentication: Authentication.State
    }

    private lateinit var dependencies: Dependencies

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.createSource(contentResolver, uri)
                .let { source -> ImageDecoder.decodeBitmap(source) }
        }
        else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        uploadImage(bitmap)
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
            val album = getAlbum(albumId = dependencies.albumId)

            findViewById<TextView>(R.id.albumTitle).text = album.title
            adapter.updateImages(album.images)
        }
    }

    private suspend fun getAlbum(albumId: String): Album = withContext(Dispatchers.IO) {
        val bytes = "https://api.imgur.com/3/album/$albumId".asUrlReadBytes {
            (this as HttpURLConnection).requestMethod = "GET"
            addRequestProperty("Authorization", "Client-ID 6b1112a4f9783ad")
        }
        val string = String(bytes)
        val json = JSONObject(string)

        json.asImgurResponse { asAlbum() }
    }

    private fun <R> JSONObject.asImgurResponse(data: JSONObject.() -> R): R =
        if (getBoolean("success")) {
            getJSONObject("data").data()
        } else error("http code ${getInt("status")}")

    private fun JSONObject.asAlbum() = Album(
        title = getString("title"),
        description = getString("description"),
        images = getJSONArray("images").run {
            (0 until length()).map {
                getJSONObject(it).asImage()
            }
        }
    )

    private fun JSONObject.asImage() = Image(
        title = getString("title"),
        description = getString("description"),
        link = getString("link")
    )

    /**
     * Upload an image to the currently authenticated Imgur account.
     *
     * @return A reference to the upload job.
     */
    private fun uploadImage(image: Bitmap) = GlobalScope.launch {

    }
}
