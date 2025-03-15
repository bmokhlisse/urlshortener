package com.example.urlshortener.util

import java.net.URI

object UrlValidator {

     fun isValidUrl(url: String): Boolean {
        return try {
            URI(url).scheme?.let { it == "http" || it == "https" } ?: false
        } catch (e: Exception) {
            false
        }
    }
}