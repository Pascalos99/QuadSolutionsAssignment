package com.github.pascalos99.quad_assignment_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestClientConfig {
	
	@Bean
	RestClient.Builder triviaRestClient(@Value("${trivia.api.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl);
	}
	
	@Bean
	RestClient.Builder quoteRestClient(@Value("${quote.api.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl);
	}
	
}
