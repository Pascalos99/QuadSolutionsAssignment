package com.github.pascalos99.quad_assignment_backend.trivia;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories;
import com.github.pascalos99.quad_assignment_backend.trivia.model.TokenResponse;

@Component
public class TriviaClient {
	
	private final RestClient restClient;
	
	public TriviaClient(@Qualifier("triviaRestClient") RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}
	
	public ApiResponse getQuestions(
			int amount,
			@Nullable Integer category,
			@Nullable String difficulty,
			@Nullable String type,
			@Nullable String encode,
			@Nullable String token
		)
	{	
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api.php")
						.queryParam("amount", amount)
						.queryParamIfPresent("category", Optional.ofNullable(category))
						.queryParamIfPresent("difficulty", Optional.ofNullable(difficulty))
						.queryParamIfPresent("type", Optional.ofNullable(type))
						.queryParamIfPresent("encode", Optional.ofNullable(encode))
						.queryParamIfPresent("token", Optional.ofNullable(token))
						.build())
				.retrieve()
				.body(ApiResponse.class);
	}
	
	public Categories getCategories() {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api_category.php")
						.build())
				.retrieve()
				.body(Categories.class);
	}
	
	public String getToken() {
		TokenResponse resp = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api_token.php")
						.queryParam("command", "request")
						.build())
				.retrieve()
				.body(TokenResponse.class);
		
		if (resp.response_code() == 0)
			return resp.token();
		return null;
	}
	
	public boolean resetToken(String token) {
		if (token == null) return false;
		TokenResponse resp = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api_token.php")
						.queryParam("command", "reset")
						.queryParam("token", token)
						.build())
				.retrieve()
				.body(TokenResponse.class);
		
		return resp.response_code() == 0;
	}
	
	//// Making a request:
	// Possible values for 'type':
	public static final String TYPE_BOOL = "boolean";
	public static final String TYPE_MULT = "multiple";
	
	// Possible values for 'difficulty':
	public static final String DIFFICULTY_1 = "easy";
	public static final String DIFFICULTY_2 = "medium";
	public static final String DIFFICULTY_3 = "hard";
	
	// Possible values for 'encoding':
	public static final String ENCODE_HTML = null;
	public static final String ENCODE_LEGACY = "urlLegacy";
	public static final String ENCODE_URL = "url3986";
	public static final String ENCODE_BASE64 = "base64";

}
