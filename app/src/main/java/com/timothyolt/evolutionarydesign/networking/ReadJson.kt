package com.timothyolt.evolutionarydesign.networking

import org.json.JSONObject
import java.net.URLConnection

suspend fun URLConnection.readJson(): JSONObject {
    val bytes = readBytes()
    val string = String(bytes)
    return JSONObject(string)
}
