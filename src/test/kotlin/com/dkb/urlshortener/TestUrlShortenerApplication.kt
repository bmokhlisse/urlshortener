package com.dkb.urlshortener

import com.dkb.urlshortener.config.TestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<UrlShortenerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
