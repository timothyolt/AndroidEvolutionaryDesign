package com.timothyolt.evolutionarydesign.auth

class Authentication {
    var state: State? = null
    data class State(
        val accessToken: String,
        val tokenType: String?,
        val expiresIn: String?
    )
}
