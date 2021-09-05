package com.timothyolt.evolutionarydesign.album

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.timothyolt.evolutionarydesign.networking.asUrl
import com.timothyolt.evolutionarydesign.networking.connection
import com.timothyolt.evolutionarydesign.networking.readBytes
import com.timothyolt.evolutionarydesign.networking.writeBytes
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URLConnection

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
        val json = "https://api.imgur.com/3/album/$albumId".asUrl().connection {
            addRequestProperty("Authorization", "Client-ID 6b1112a4f9783ad")
            readJson()
        }

        json.asImgurResponse { asAlbum() }
    }

    /**
     * Upload an image to the currently authenticated Imgur account.
     *
     * @return A reference to the upload job.
     */
    private fun uploadImage(image: Bitmap) = GlobalScope.launch {
        // might be better to stream this directly from the local file
        val pngBase64String = image.toCompressedBase64()

        "".asUrl().connection<HttpURLConnection, ByteArray>{
            writeFormData(listOf(
                "image" to pngBase64String,
                "type" to "base64"
            ))
            val responseCode = responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                readBytes()
            } else {
                error("Non-OK status $responseCode")
            }
        }
    }
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

private suspend fun URLConnection.readJson(): JSONObject {
    val bytes = readBytes()
    val string = String(bytes)
    return JSONObject(string)
}

private fun Bitmap.toCompressedBase64(): String {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 0, outputStream)
    val pngBytes = outputStream.toByteArray()
    val pngBase64 = Base64.encode(pngBytes, 0)
    return String(pngBase64)
}

private suspend fun URLConnection.writeFormData(fields: List<Pair<String, String>>) {
    val newLine = "\r\n"
    val boundary = "boundary"

    val byteStream = ByteArrayOutputStream()
    val writer = PrintWriter(byteStream)

    for (field in fields) {
        writer.writeFormPart(field, newLine)
    }

    writer.writeFormFinish(boundary, newLine)

    writeBytes("multipart/form-data; boundary=$boundary", byteStream.toByteArray())
}

private fun PrintWriter.writeFormPart(
    field: Pair<String, String>,
    newLine: String
) {
    append("Content-Disposition: form-data; name=\"${field.first}\"").append(newLine)
    append("Content-Type: text/plain; charset=${Charsets.UTF_8}").append(newLine)
    append(newLine)
    append(field.second).append(newLine)
    flush()
}

private fun PrintWriter.writeFormFinish(boundary: String, newLine: String) {
    flush()
    append("--$boundary--").append(newLine)
    close()
}
