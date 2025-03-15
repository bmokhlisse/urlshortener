package com.example.urlshortener.controller

import com.example.urlshortener.config.TestcontainersConfiguration
import com.example.urlshortener.dto.request.ShortenRequest
import com.example.urlshortener.repository.UrlMappingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class UrlShorteningControllerIntegrationTest {

    @Autowired
    private lateinit var repository: UrlMappingRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()
    private lateinit var baseUrl: String
    private val testUrl = "https://example.com"

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port"
        repository.deleteAll()
        redisTemplate.connectionFactory?.connection?.flushAll() // TODO
    }

    @Test
    fun `should shorten a URL` () {
        // Given
        val request = ShortenRequest(url = testUrl)
        val uri = "$baseUrl/shorten"

        // When
        val response = restTemplate.postForEntity(uri, request, Map::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val shortUrl = response.body?.get("shortUrl") as String

        val shortId = shortUrl.substringAfterLast("/")
        val dbEntry = repository.findByShortId(shortId)
        assertTrue(dbEntry.isPresent, "shortened URL should be stored in database")
        assertEquals(testUrl, dbEntry.get().longUrl)

        val cachedUrl = redisTemplate.opsForValue().get(shortId)
        assertNotNull(cachedUrl, "shortened URL should be cached in cache")
        assertEquals(testUrl, cachedUrl)
    }

    @Test
    fun  `should retrieve original URL and 320 redirect` () {
        // Given
        val request = ShortenRequest(url = testUrl)
        val shortenUri = "$baseUrl/shorten"
        val response = restTemplate.postForEntity(shortenUri, request, Map::class.java)
        val shortUrl = response.body?.get("shortUrl") as String
        val shortId = shortUrl.substringAfterLast("/")

        // When
        val redirectUri = UriComponentsBuilder.fromHttpUrl("$baseUrl/$shortId").toUriString()
        val redirectResponse = restTemplate.getForEntity(redirectUri, Void::class.java)

        // Then
        assertEquals(HttpStatus.FOUND, redirectResponse.statusCode)
        assertEquals(testUrl, redirectResponse.headers.location.toString())
    }

    @Test
    fun `should return 404 for non-existent shortId`() {
        // Given
        val nonExistentShortId = "99999"
        val uri = "$baseUrl/$nonExistentShortId"

        // When
        val exception = assertThrows<HttpClientErrorException.NotFound> {
            restTemplate.getForEntity(uri, Void::class.java)
        }

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }
}