package com.dkb.urlshortener.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "url_mapping")
data class UrlMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "short_id", unique = true, nullable = false)
    val shortId: String,

    @Column(name = "long_url", nullable = false)
    val longUrl: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
