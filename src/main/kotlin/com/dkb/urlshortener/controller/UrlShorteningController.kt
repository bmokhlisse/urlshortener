package com.dkb.urlshortener.controller

import com.dkb.urlshortener.dto.request.ShortenRequest
import com.dkb.urlshortener.dto.response.ShortenResponse
import com.dkb.urlshortener.service.UrlShorteningService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/")
class UrlShorteningController(
    private val urlShorteningService: UrlShorteningService,
    @Value("\${app.domain}")
    private val baseDomain: String
) {

    @PostMapping("shorten")
    fun shortenUrl(@RequestBody shortenRequest: ShortenRequest): ResponseEntity<ShortenResponse> {

        val shortId = urlShorteningService.shortenUrl(shortenRequest.url)
        return ResponseEntity.ok(ShortenResponse("$baseDomain/$shortId"))
    }

    @GetMapping("{shortId}")
    fun getOriginalUrl(@PathVariable shortId: String): ResponseEntity<Unit> {

        val longUrl = urlShorteningService.getLongUrl(shortId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", longUrl).build()
    }
}