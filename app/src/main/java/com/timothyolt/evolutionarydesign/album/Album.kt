package com.timothyolt.evolutionarydesign.album

import com.timothyolt.evolutionarydesign.image.Image

data class Album(
    val title: String,
    val description: String,
    val images: List<Image>
)