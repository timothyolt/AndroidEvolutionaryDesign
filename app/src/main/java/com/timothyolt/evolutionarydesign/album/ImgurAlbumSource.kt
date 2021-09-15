package com.timothyolt.evolutionarydesign.album

import com.timothyolt.evolutionarydesign.image.Image
import com.timothyolt.evolutionarydesign.networking.asUrl
import com.timothyolt.evolutionarydesign.networking.connection
import com.timothyolt.evolutionarydesign.networking.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ImgurAlbumSource : AlbumSource {
    override suspend fun getAlbum(albumId: String): Album {
        return getAlbumFromNetwork(albumId)
    }

    private suspend fun getAlbumFromNetwork(albumId: String): Album = withContext(Dispatchers.IO) {
        val bytes = "https://api.imgur.com/3/album/$albumId"
            .asUrl()
            .connection {
                addRequestProperty("Authorization", "Client-ID 6b1112a4f9783ad")

                readBytes()
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
}