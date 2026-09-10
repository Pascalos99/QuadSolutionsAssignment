package com.github.pascalos99.quad_assignment_backend.quote_example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IncludeIfEnabled
@RestController
@RequestMapping("/quote")
public class QuoteController {
	
	private final QuoteClient quoteClient;
	
	public QuoteController(QuoteClient quoteClient) {
		this.quoteClient = quoteClient;
	}
	
	@GetMapping
	public Quote getQuote() {
		return quoteClient.getRandomQuote();
	}
	
}
