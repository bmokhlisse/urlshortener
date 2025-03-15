package com.example.urlshortener.repository

import com.example.urlshortener.model.UrlMapping
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UrlMappingRepository : JpaRepository<UrlMapping, Long> {

    fun findByShortId(shortId: String): Optional<UrlMapping>
}
