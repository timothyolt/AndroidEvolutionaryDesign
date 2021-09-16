package com.timothyolt.evolutionarydesign.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.net.URLConnection

data class FormData(val fields: List<Field>) {
    data class Field(val name: String, val contents: String)
}

suspend fun URLConnection.write(formData: FormData) {
    val byteStream = ByteArrayOutputStream()
    val writer = PrintWriter(byteStream, true)

    for (field in formData.fields) {
        writer.writeFormPart(field, BOUNDARY, NEW_LINE)
    }

    writer.writeFormFinish(BOUNDARY, NEW_LINE)

    val bytes = byteStream.toByteArray()

    write("multipart/form-data; boundary=$BOUNDARY", bytes)
}

private fun PrintWriter.writeFormPart(
    field: FormData.Field,
    boundary: String,
    newLine: String
) {
    append("--$boundary").append(newLine)
    append("Content-Disposition: form-data; name=\"${field.name}\";").append(newLine)
    append("Content-Type: text/plain;").append(newLine)
    append(newLine)
    append(field.contents).append(newLine)
    flush()
}

private fun PrintWriter.writeFormFinish(boundary: String, newLine: String) {
    flush()
    append("--$boundary--").append(newLine)
    close()
}

@Suppress("BlockingMethodInNonBlockingContext")
private suspend fun URLConnection.write(
    contentType: String,
    bytes: ByteArray
) = withContext(Dispatchers.IO) {
    doOutput = true
    setRequestProperty("Content-Type", contentType)
    getOutputStream()
        .buffered()
        .use { it.write(bytes) }
}

private const val NEW_LINE = "\r\n"
private const val BOUNDARY = "boundary"
