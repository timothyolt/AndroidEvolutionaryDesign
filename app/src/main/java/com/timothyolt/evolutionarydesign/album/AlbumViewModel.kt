package com.timothyolt.evolutionarydesign.album

import kotlinx.coroutines.flow.Flow

interface AlbumViewModel {
    val albumState: Flow<Album>
    suspend fun requestAlbum(albumId: String)
}
