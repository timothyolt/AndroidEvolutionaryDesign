package com.timothyolt.evolutionarydesign

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

interface AlbumRepository {
    data class Album(
        val title: String,
        val images: List<Image>
    )

    suspend fun getAlbum(): Album
}

class NetworkAlbumRepository : AlbumRepository {

    override suspend fun getAlbum(): AlbumRepository.Album {
        val albumJson = getAlbumJson()
        val data = albumJson.getJSONObject("data")
        return AlbumRepository.Album(
            title = data.getString("title"),
            images = data.getJSONArray("images")
                .run { (0 until length()).map { getJSONObject(it) } }
                .map { getImage(it.getString("link")) }
        )
    }

    private suspend fun getAlbumJson(): JSONObject {
        return withContext(Dispatchers.IO) {
            val albumBytes = URL("https://api.imgur.com/3/album/xxrXmHr")
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

    private suspend fun getImage(link: String): Image = withContext(Dispatchers.IO) {
        val bytes = URL(link)
            .openConnection()
            .getInputStream()
            .use { it.readBytes() }
        Image(bytes)
    }
}
