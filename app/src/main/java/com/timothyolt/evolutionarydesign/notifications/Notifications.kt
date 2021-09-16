package com.timothyolt.evolutionarydesign.notifications

data class Notifications(
    val channels: Channels,
    val ids: Ids
) {

    data class Channels(
        val uploadProgress: String
    )

    data class Ids(
        val upload: Int
    )

}
