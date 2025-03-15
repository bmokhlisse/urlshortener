package com.example.urlshortener

import com.example.urlshortener.config.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class UrlShortenerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
