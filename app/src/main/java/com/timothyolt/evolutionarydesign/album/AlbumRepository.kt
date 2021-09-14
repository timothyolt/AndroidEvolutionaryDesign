package com.timothyolt.evolutionarydesign.album

import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    val albumFlow: Flow<Album>
}
