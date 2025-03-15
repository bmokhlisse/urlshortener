package com.example.urlshortener.util

import java.security.MessageDigest

object HashUtils {
    private const val SHORT_URL_LENGTH = 6

    fun generateShortId(longUrl: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(longUrl.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }.take(SHORT_URL_LENGTH)
    }
}