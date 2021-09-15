package com.timothyolt.evolutionarydesign

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AlbumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            id = R.id.helloText
            text = "Hello World!"
        })
    }

    private suspend fun getAlbumJson(): JSONObject {

        return withContext(Dispatchers.IO) {
            val albumBytes = URL("https://api.imgur.com/3/album/xRvUk7p")
                .openConnection()
                .run { this as HttpURLConnection }
                .apply {
                    addRequestProperty("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
                }
                .run {
                    if (responseCode == 200) {
                        getInputStream()
                            .use { it.readBytes() }
                    } else {
                        error(responseMessage)
                    }
                }
            JSONObject(String(albumBytes))
        }
    }
}
