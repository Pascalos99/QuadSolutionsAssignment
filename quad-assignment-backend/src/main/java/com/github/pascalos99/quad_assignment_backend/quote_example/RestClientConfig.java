package com.github.pascalos99.quad_assignment_backend.quote_example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestClientConfig {
	
	@Bean
	RestClient quoteRestClient(RestClient.Builder builder, @Value("${quote.api.base-url}") String baseUrl) {
		return builder.baseUrl(baseUrl).build();
	}
	
	
}
