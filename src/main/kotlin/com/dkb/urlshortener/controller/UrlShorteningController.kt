package com.dkb.urlshortener.controller

import com.dkb.urlshortener.dto.request.ShortenRequest
import com.dkb.urlshortener.dto.response.ShortenResponse
import com.dkb.urlshortener.service.UrlShorteningService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
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

    @Operation(
        summary = "Shorten a URL",
        description = "Generates a short URL for a long URL.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The URL to shorten",
            required = true,
            content = [Content(schema = Schema(implementation = ShortenRequest::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Successfully shortened URL"),
            ApiResponse(responseCode = "400", description = "Invalid URL"),
        ]
    )
    @PostMapping("shorten")
    fun shortenUrl(@RequestBody shortenRequest: ShortenRequest): ResponseEntity<ShortenResponse> {

        val shortId = urlShorteningService.shortenUrl(shortenRequest.url)
        return ResponseEntity.ok(ShortenResponse("$baseDomain/$shortId"))
    }

    @Operation(
        summary = "Resolve a shortened URL",
        description = "Redirects to the original long URL if the short ID exists.",
        responses = [
            ApiResponse(responseCode = "302", description = "Redirect to the original URL"),
            ApiResponse(responseCode = "404", description = "Short ID not found")
        ]
    )    @GetMapping("{shortId}")
    fun getOriginalUrl(@PathVariable shortId: String): ResponseEntity<Unit> {

        val longUrl = urlShorteningService.getLongUrl(shortId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", longUrl).build()
    }
}