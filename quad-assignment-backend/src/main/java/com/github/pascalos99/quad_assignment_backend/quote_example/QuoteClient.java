package com.github.pascalos99.quad_assignment_backend.quote_example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@IncludeIfEnabled
@Component
public class QuoteClient {
	
	private final RestClient restClient;
	
	public QuoteClient(@Qualifier("quoteRestClient") RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
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
