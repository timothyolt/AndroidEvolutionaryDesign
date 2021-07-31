package com.timothyolt.evolutionarydesign

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val bitmap = when (Build.VERSION.SDK_INT) {
            in 0..27 -> {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            else -> {
                ImageDecoder.createSource(contentResolver, uri)
                    .let { source -> ImageDecoder.decodeBitmap(source) }
            }
        }

        uploadImage(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val result = getImage()

            val image = BitmapFactory.decodeByteArray(result, 0, result.size)

            findViewById<ImageView>(R.id.image).setImageBitmap(image)
        }

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private suspend fun getImage(): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL("https://i.imgur.com/GSwTJrM.jpeg")
            .let { it.openConnection() as HttpURLConnection }
            .apply { requestMethod = "GET" }

        val inputStream = BufferedInputStream(connection.inputStream)

        val data = inputStream.use { inputStream.readBytes() }

        connection.disconnect()

        data
    }

    /**
     * Upload an image to the currently authenticated Imgur account.
     *
     * @return A reference to the upload job.
     */
    private fun uploadImage(image: Bitmap) = GlobalScope.launch {

    }
}
