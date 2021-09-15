package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

interface AlbumService {
    suspend fun getAlbumTitle(): String
}

class NetworkAlbumService : AlbumService {
    override suspend fun getAlbumTitle(): String {
        val albumJson = getAlbumJson()
        return albumJson.getJSONObject("data").getString("title")
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
