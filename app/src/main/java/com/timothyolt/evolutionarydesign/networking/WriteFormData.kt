package com.timothyolt.evolutionarydesign.networking

import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.net.URLConnection

suspend fun URLConnection.writeFormData(fields: List<Pair<String, String>>) {
    val byteStream = ByteArrayOutputStream()
    val writer = PrintWriter(byteStream, true)

    for (field in fields) {
        writer.writeFormPart(field, BOUNDARY, NEW_LINE)
    }

    writer.writeFormFinish(BOUNDARY, NEW_LINE)

    val bytes = byteStream.toByteArray()

    writeBytes("multipart/form-data; boundary=$BOUNDARY", bytes)
}

private fun PrintWriter.writeFormPart(
    field: Pair<String, String>,
    boundary: String,
    newLine: String
) {
    append("--$boundary").append(newLine)
    append("Content-Disposition: form-data; name=\"${field.first}\";").append(newLine)
    append("Content-Type: text/plain;").append(newLine)
    append(newLine)
    append(field.second).append(newLine)
    flush()
}

private fun PrintWriter.writeFormFinish(boundary: String, newLine: String) {
    flush()
    append("--$boundary--").append(newLine)
    close()
}

private const val NEW_LINE = "\r\n"
private const val BOUNDARY = "boundary"
