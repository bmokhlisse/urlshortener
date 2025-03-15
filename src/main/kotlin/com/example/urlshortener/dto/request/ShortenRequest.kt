package com.example.urlshortener.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request to shorten a URL")
data class ShortenRequest(
    @Schema(
        description = "The URL to be shortened",
        example = "https://example.com"
    )
    val url: String
)