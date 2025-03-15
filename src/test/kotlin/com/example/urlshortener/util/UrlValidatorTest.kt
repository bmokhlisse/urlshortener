package com.example.urlshortener.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UrlValidatorTest {

    @Test
    fun `should return true for valid URLs`() {
        assertTrue(UrlValidator.isValidUrl("http://example.com"))
        assertTrue(UrlValidator.isValidUrl("https://example.com"))
        assertTrue(UrlValidator.isValidUrl("https://sub.example.com/path?query=value"))
    }

    @Test
    fun `should return false for invalid URLs`() {
        assertFalse(UrlValidator.isValidUrl("htp://incorrect-protocol.com"))
        assertFalse(UrlValidator.isValidUrl("http//missing-colon.com"))
        assertFalse(UrlValidator.isValidUrl("ftp://not-allowed-protocol.com"))
        assertFalse(UrlValidator.isValidUrl("just-text"))
        assertFalse(UrlValidator.isValidUrl(""))
        // TODO to be extended
    }
}
