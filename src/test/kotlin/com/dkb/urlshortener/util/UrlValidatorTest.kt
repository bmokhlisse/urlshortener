package com.dkb.urlshortener.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UrlValidatorTest {

    @Test
    fun `should return true for valid URLs`() {
        assertTrue(UrlValidator.isValidUrl("http://dkb.com"))
        assertTrue(UrlValidator.isValidUrl("https://dkb.com"))
        assertTrue(UrlValidator.isValidUrl("https://sub.dkb.com/path?query=value"))
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
