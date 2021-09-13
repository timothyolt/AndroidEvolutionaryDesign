package com.timothyolt.evolutionarydesign.album

import com.timothyolt.evolutionarydesign.image.Image
import com.timothyolt.evolutionarydesign.networking.asUrl
import com.timothyolt.evolutionarydesign.networking.connection
import com.timothyolt.evolutionarydesign.networking.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection

class AlbumViewModelImpl : AlbumViewModel {
    private val _albumState = MutableStateFlow(Album.empty())
    override val albumState: Flow<Album>
        get() = _albumState

    override suspend fun requestAlbum(albumId: String) {
        val album = getAlbum(albumId)
        _albumState.emit(album)
    }

    private suspend fun getAlbum(albumId: String): Album = withContext(Dispatchers.IO) {
        val bytes = "https://api.imgur.com/3/album/$albumId"
            .asUrl()
            .connection {
                (this as HttpURLConnection).requestMethod = "GET"
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
