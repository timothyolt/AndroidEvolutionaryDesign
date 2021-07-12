package com.timothyolt.evolutionarydesign

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val result = getImage()

            val image = BitmapFactory.decodeByteArray(result, 0, result.size)

            findViewById<ImageView>(R.id.image)
                .setImageBitmap(image)
        }
    }

    private suspend fun getImage(): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL("https://i.imgur.com/GSwTJrM.jpeg")
            .let { it.openConnection() as HttpURLConnection }
            .apply { requestMethod = "GET" }

        val inputStream = BufferedInputStream(connection.inputStream)

        val data = inputStream.readBytes()

        inputStream.close()

        connection.disconnect()

        data
    }
}
