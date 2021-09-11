package com.timothyolt.evolutionarydesign.networking

import java.io.InputStream
import java.io.OutputStream

fun InputStream.transferTo(outputStream: OutputStream): Long {
    var transferred: Long = 0
    val buffer = ByteArray(8192)
    var read: Int
    while (read(buffer, 0, 8192).also { read = it } >= 0) {
        outputStream.write(buffer, 0, read)
        transferred += read
    }
    return transferred
}
