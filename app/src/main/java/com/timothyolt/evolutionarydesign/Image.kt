package com.timothyolt.evolutionarydesign

data class Image(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode() = bytes.contentHashCode()
}