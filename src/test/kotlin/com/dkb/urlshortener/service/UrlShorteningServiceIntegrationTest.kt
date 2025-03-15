package com.dkb.urlshortener.service

import com.dkb.urlshortener.config.TestcontainersConfiguration
import com.dkb.urlshortener.exception.InvalidUrlException
import com.dkb.urlshortener.exception.ShortUrlNotFoundException
import com.dkb.urlshortener.repository.UrlMappingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class UrlShorteningServiceIntegrationTest {

    @Autowired
    private lateinit var urlShorteningService: UrlShorteningService

    @Autowired
    private lateinit var repository: UrlMappingRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private val testUrl = "https://dkb.com"

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        redisTemplate.connectionFactory?.connection?.flushAll() // TODO
    }

    @Test
    fun `should shorten an URL and store it in database`() {
        // When
        val shortId = urlShorteningService.shortenUrl(testUrl)

        // Then
        val actualUrl = repository.findByShortId(shortId)
        assertTrue(actualUrl.isPresent, "shortened Url should be persisted in database")
        assertEquals(testUrl, actualUrl.get().longUrl)
    }

    @Test
    fun `should cache shortened URL in cache`() {
        // When
        val shortId = urlShorteningService.shortenUrl(testUrl)

        // Then
        val actualUrl = redisTemplate.opsForValue().get(shortId)
        assertNotNull(actualUrl)
        assertEquals(testUrl, actualUrl)
    }

    @Test
    fun `should retrieve URL from database when it does not exist in cache`() {
        // Given
        val shortId = urlShorteningService.shortenUrl(testUrl)
        redisTemplate.delete(shortId)

        // When
        val actualLongUrl = urlShorteningService.getLongUrl(shortId)

        // Then
        assertNotNull(actualLongUrl, "getLongUrl should return stored url even when it does not exist in cache")
        assertEquals(testUrl, actualLongUrl)
    }

    @Test
    fun `should throw ShortUrlNotFoundException when shortId does not exist` () {
        // Given
        val nonExistingShortId = "nonExistingId"

        // When & Then
        val exception = assertThrows<ShortUrlNotFoundException> {
            urlShorteningService.getLongUrl(nonExistingShortId)
        }
        assertEquals("No URL found for shortId: $nonExistingShortId", exception.message)
    }

    @Test
    fun `should throw InvalidUrlException when given url is not valid` () {
        // Given
        val nonValidUrl = "http//www.dkb.com"

        // When & Then
        val exception = assertThrows<InvalidUrlException> {
            urlShorteningService.shortenUrl(nonValidUrl)
        }
        assertEquals("The provided URL is not valid: $nonValidUrl", exception.message)
    }

}