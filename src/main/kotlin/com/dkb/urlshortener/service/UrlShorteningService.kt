package com.dkb.urlshortener.service

import com.dkb.urlshortener.exception.InvalidUrlException
import com.dkb.urlshortener.exception.ShortUrlNotFoundException
import com.dkb.urlshortener.model.UrlMapping
import com.dkb.urlshortener.repository.UrlMappingRepository
import com.dkb.urlshortener.util.HashUtils
import com.dkb.urlshortener.util.UrlValidator
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.net.URI
import java.util.concurrent.TimeUnit

@Service
class UrlShorteningService(
    private val repository: UrlMappingRepository,
    private val redisTemplate: StringRedisTemplate
) {

    companion object{
        private const val CACHE_EXPIRY_DAYS = 30L

    }

    fun shortenUrl(longUrl: String): String {

        if (!UrlValidator.isValidUrl(longUrl)) {
            throw InvalidUrlException("The provided URL is not valid: $longUrl")
        }

        val shortId = HashUtils.generateShortId(longUrl)

        if(!repository.findByShortId(shortId).isPresent) {
            val urlMapping = UrlMapping(shortId = shortId, longUrl = longUrl)
            // persist entity
            repository.save(urlMapping)

            // cache in redis
            redisTemplate.opsForValue().set(shortId, longUrl, CACHE_EXPIRY_DAYS, TimeUnit.DAYS)
        }

        return shortId
    }

    fun getLongUrl(shortId: String): String? {

        val cachedUrl = redisTemplate.opsForValue().get(shortId)
        if(cachedUrl != null) return cachedUrl

        val urlMapping = repository.findByShortId(shortId).orElseThrow { ShortUrlNotFoundException("No URL found for shortId: $shortId") }
        // cache in redis
        redisTemplate.opsForValue().set(shortId, urlMapping.longUrl, CACHE_EXPIRY_DAYS, TimeUnit.DAYS)

        return urlMapping.longUrl
    }
}