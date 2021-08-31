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

    data class Dependencies(val httpUrl: String)

    private lateinit var dependencies: Dependencies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = requireInjector().inject(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val result = getImage()

            val image = BitmapFactory.decodeByteArray(result, 0, result.size)

            findViewById<ImageView>(R.id.image)
                .setImageBitmap(image)
        }
    }

    private suspend fun getImage(): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL(dependencies.httpUrl)
            .let { it.openConnection() as HttpURLConnection }
            .apply { requestMethod = "GET" }

        val inputStream = BufferedInputStream(connection.inputStream)

        val data = inputStream.use { inputStream.readBytes() }

        connection.disconnect()

        data
    }
}
