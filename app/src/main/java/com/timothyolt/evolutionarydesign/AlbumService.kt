package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Image(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode() = bytes.contentHashCode()
}

interface AlbumService {
    data class Album(
        val title: String,
        val image: Image
    )
    suspend fun getAlbum(): Album
}

class NetworkAlbumService : AlbumService {
    override suspend fun getAlbum(): AlbumService.Album {
        val albumJson = getAlbumJson()
        val data = albumJson.getJSONObject("data")
        return AlbumService.Album(
            title = data.getString("title"),
            image = getImage(
                data.getJSONArray("images")
                    .getJSONObject(0)
                    .getString("link")
            )
        )
    }

    private suspend fun getImage(link: String) = withContext(Dispatchers.IO) {
        val bytes = URL(link)
            .openConnection()
            .getInputStream()
            .use { it.readBytes() }
        Image(bytes)
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
