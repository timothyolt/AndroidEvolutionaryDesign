package com.timothyolt.evolutionarydesign.album

interface AlbumSource {
    suspend fun getAlbum(albumId: String): Album
}
