package com.timothyolt.evolutionarydesign.album

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AlbumRepositoryImpl(
    private val albumId: String,
    private val albumSource: AlbumSource
) : AlbumRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private val mutableAlbumFlow = MutableSharedFlow<Album>(replay = 1)
    override val albumFlow: Flow<Album>
        get() = mutableAlbumFlow

    init {
        coroutineScope.launch {
            getAlbum(albumId)
        }
    }

    private suspend fun getAlbum(albumId: String) {
        val album = albumSource.getAlbum(albumId)

        mutableAlbumFlow.emit(album)
    }
}
