package com.example.urlshortener.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response containing the shortened URL")
data class ShortenResponse(
    @Schema(
        description = "The shortened URL",
        example = "https://kurz.li/abcd123"
    )
    val shortUrl: String
)