package com.example.urlshortener

import com.example.urlshortener.config.TestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<UrlShortenerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
