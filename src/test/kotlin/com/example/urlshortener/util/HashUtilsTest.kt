package com.example.urlshortener.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HashUtilsTest {

    @Test
    fun `should generate the same short IDs for the same input`() {
        val url = "https://example.com"
        val shortId1 = HashUtils.generateShortId(url)
        val shortId2 = HashUtils.generateShortId(url)

        assertEquals(shortId1, shortId2, "Short ID should be deterministic")
    }

    @Test
    fun `should generate exact 6-character short IDs`() {
        val shortId = HashUtils.generateShortId("https://example.com")
        assertEquals(6, shortId.length, "Short ID should be exactly 6 characters")
    }
}
