package com.timothyolt.evolutionarydesign.album

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.timothyolt.evolutionarydesign.BuildConfig
import com.timothyolt.evolutionarydesign.image.Image
import com.timothyolt.evolutionarydesign.image.ImageAdapter
import com.timothyolt.evolutionarydesign.R
import com.timothyolt.evolutionarydesign.auth.Authentication
import com.timothyolt.evolutionarydesign.networking.*
import com.timothyolt.evolutionarydesign.requireInjector
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URLConnection
import java.util.Base64

class AlbumActivity : AppCompatActivity() {

    interface Dependencies {
        val albumId: String
        val authentication: Authentication.State
    }

    private lateinit var dependencies: Dependencies

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uploadImage(contentResolver.openInputStream(uri)!!)
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
            addRequestProperty("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
            readJson()
        }

        json.asImgurResponse { asAlbum() }
    }

    /**
     * Upload an image to the currently authenticated Imgur account.
     *
     * @return A reference to the upload job.
     */
    private fun uploadImage(image: InputStream) = GlobalScope.launch {
        // ok to write this to a string rather than totally streaming it, because Imgur caps uploads to 10MB
        val imageBytes = ByteArrayOutputStream()
        image.transferTo(Base64.getEncoder().wrap(imageBytes))
        val pngBase64String = String(imageBytes.toByteArray())

        "https://api.imgur.com/3/upload".asUrl().connection<HttpURLConnection, Unit> {
            addRequestProperty("Authorization", "Bearer ${dependencies.authentication.accessToken}")
            writeFormData(listOf(
                "image" to pngBase64String,
                "type" to "base64"
            ))
            val responseCode = responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bytes = readBytes()
                Log.w("ImgurUpload", String(bytes))
            } else {
                val errorBody = readErrorBytes()
                Log.e("ImgurUpload", String(errorBody))
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

private fun InputStream.transferTo(outputStream: OutputStream): Long {
    var transferred: Long = 0
    val buffer = ByteArray(8192)
    var read: Int
    while (read(buffer, 0, 8192).also { read = it } >= 0) {
        outputStream.write(buffer, 0, read)
        transferred += read
    }
    return transferred
}

private suspend fun URLConnection.writeFormData(fields: List<Pair<String, String>>) {
    val newLine = "\r\n"
    val boundary = "boundary"

    val byteStream = ByteArrayOutputStream()
    val writer = PrintWriter(byteStream, true)

    for (field in fields) {
        writer.writeFormPart(field, boundary, newLine)
    }

    writer.writeFormFinish(boundary, newLine)

    val bytes = byteStream.toByteArray()
    val string = String(bytes)

    writeBytes("multipart/form-data; boundary=$boundary", bytes)
}

private fun PrintWriter.writeFormPart(
    field: Pair<String, String>,
    boundary: String,
    newLine: String
) {
    append("--$boundary").append(newLine);
    append("Content-Disposition: form-data; name=\"${field.first}\";").append(newLine)
    append("Content-Type: text/plain;").append(newLine)
    append(newLine)
    append(field.second).append(newLine)
    flush()
}

private fun PrintWriter.writeFormFinish(boundary: String, newLine: String) {
    flush()
    append("--$boundary--").append(newLine)
    close()
}
