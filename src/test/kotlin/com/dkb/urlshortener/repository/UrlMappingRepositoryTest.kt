package com.dkb.urlshortener.repository

import com.dkb.urlshortener.model.UrlMapping
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.util.*

@DataJpaTest
@Import(com.dkb.urlshortener.config.TestcontainersConfiguration::class)
class UrlMappingRepositoryTest {

    @Autowired
    private lateinit var repository: UrlMappingRepository

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `should save and retrieve UrlMapping`() {
        // Given
        val urlMapping = UrlMapping(shortId = "abc123", longUrl = "https://dkb.com")
        val saved = repository.save(urlMapping)

        // When
        val found = repository.findByShortId("abc123")

        // Then
        assertTrue(found.isPresent)
        assertEquals("https://dkb.com", found.get().longUrl)
        assertEquals(saved.id, found.get().id)
    }

    @Test
    fun `should return empty when shortId does not exist`() {
        // When
        val result: Optional<UrlMapping> = repository.findByShortId("nonexistent")

        // Then
        assertTrue(result.isEmpty)
    }
}
