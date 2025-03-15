package com.dkb.urlshortener.service

import com.dkb.urlshortener.exception.InvalidUrlException
import com.dkb.urlshortener.exception.ShortUrlNotFoundException
import com.dkb.urlshortener.model.UrlMapping
import com.dkb.urlshortener.repository.UrlMappingRepository
import com.dkb.urlshortener.util.HashUtils
import com.dkb.urlshortener.util.UrlValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class UrlShorteningService(
    private val repository: UrlMappingRepository,
    private val redisTemplate: StringRedisTemplate
) {

    private val logger: Logger = LoggerFactory.getLogger(UrlShorteningService::class.java)

    companion object{
        private const val CACHE_EXPIRY_DAYS = 30L

    }

    fun shortenUrl(longUrl: String): String {
        logger.info("Received request to shorten URL: $longUrl")

        if (!UrlValidator.isValidUrl(longUrl)) {
            logger.warn("Invalid URL provided: $longUrl")
            throw InvalidUrlException("The provided URL is not valid: $longUrl")
        }

        var shortId: String
        do {
            shortId = HashUtils.generateShortId(longUrl + System.nanoTime())
        } while (repository.findByShortId(shortId).isPresent)
        logger.info("Generated short ID: $shortId")

        if(!repository.findByShortId(shortId).isPresent) {
            val urlMapping = UrlMapping(shortId = shortId, longUrl = longUrl)
            // persist entity
            repository.save(urlMapping)

            // cache in redis
            redisTemplate.opsForValue().set(shortId, longUrl, CACHE_EXPIRY_DAYS, TimeUnit.DAYS)
            logger.info("Stored in DB and cached in Redis: $shortId")
        }

        return shortId
    }

    fun getLongUrl(shortId: String): String? {
        logger.info("Looking up original URL for shortId: $shortId")

        val cachedUrl = redisTemplate.opsForValue().get(shortId)
        if(cachedUrl != null) {
            logger.info("Cache hit for shortId: $shortId")
            return cachedUrl
        }

        val urlMapping = repository.findByShortId(shortId).orElseThrow {
            logger.warn("ShortId not found: $shortId")
            ShortUrlNotFoundException("No URL found for shortId: $shortId")
        }
        // cache in redis
        logger.info("Cache missing. Found URL in DB, caching it: $shortId")
        redisTemplate.opsForValue().set(shortId, urlMapping.longUrl, CACHE_EXPIRY_DAYS, TimeUnit.DAYS)

        return urlMapping.longUrl
    }
}