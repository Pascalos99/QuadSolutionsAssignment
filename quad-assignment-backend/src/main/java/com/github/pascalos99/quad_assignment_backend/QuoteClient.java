package com.github.pascalos99.quad_assignment_backend;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class QuoteClient {
	
	private final RestClient restClient;
	
	public QuoteClient(@Qualifier("quoteRestClient") RestClient restClient) {
		this.restClient = restClient;
	}
	
	public Quote getRandomQuote() {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/random")
						.build())
				.retrieve()
				.body(Quote.class);
	}
	
}
