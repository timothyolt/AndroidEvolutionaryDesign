package com.timothyolt.evolutionarydesign.album

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AlbumViewModelImpl(
    private val albumRepo: AlbumRepository
) : AlbumViewModel {
    override val albumState: Flow<Album>
        get() = albumRepo.albumFlow
}
