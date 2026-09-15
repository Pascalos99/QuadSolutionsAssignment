package com.github.pascalos99.quad_assignment_backend.trivia;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories;
import com.github.pascalos99.quad_assignment_backend.trivia.model.TokenResponse;

@Component
public class TriviaClient {
	
	private final RestClient restClient;
	
	/**
	 * The minimum instant until which the Trivia-DB API
	 * will only return 'TooManyRequests' (429) errors 
	 * (response-code = 5) whenever the
	 * {@link #getQuestions(QuestionRequest)} method is called.
	 */
	private Instant apiTimeoutUntil;
	private final Duration apiRateLimit;
	
	public TriviaClient(
			@Qualifier("triviaRestClient") RestClient.Builder restClientBuilder,
			@Value("${trivia.api.rate-limit}") Duration rateLimit) {
		this.restClient = restClientBuilder.build();
		this.apiRateLimit = rateLimit;
		this.apiTimeoutUntil = Instant.now();
	}
	
	public Instant getApiTimeoutUntil() {
		return apiTimeoutUntil;
	}

    public ApiResponse getQuestions(QuestionRequest.Builder questionRequestBuilder) {
        return getQuestions(questionRequestBuilder.build());
    }
	public ApiResponse getQuestions(QuestionRequest questionRequest) {
        return getQuestions(
                questionRequest.amount(), questionRequest.category(), questionRequest.difficulty(),
                questionRequest.type(), questionRequest.encode(), questionRequest.token());
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
        AtomicBoolean hasTooManyRequests = new AtomicBoolean(false);
		ApiResponse response = restClient.get()
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
                .onStatus(status -> status.value() == 429, (rq, rs) -> {
                    hasTooManyRequests.set(true);
                })
                .body(ApiResponse.class);
        if (hasTooManyRequests.get())
        	return new ApiResponse(5, List.of());
        
        if (response.response_code() == 0)
        {
        	// The response-code is 0 only on a successful retrieval from the API.
        	// If the API was successfully retrieved from, then any request made within
        	// `rate-limit` time will result in a "TooManyRequests" HTTP status code (429)
        	apiTimeoutUntil = Instant.now().plus(apiRateLimit);
        }
        return response;
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
		
		if (resp != null && resp.response_code() == 0)
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
		
		return resp != null && resp.response_code() == 0;
	}
	
	//// Making a request:
	// Possible values for 'type':
	public static final String TYPE_BOOL = "boolean";
	public static final String TYPE_MULT = "multiple";
    public static final List<String> TYPES = List.of(TYPE_BOOL, TYPE_MULT);
	
	// Possible values for 'difficulty':
	public static final String DIFFICULTY_1 = "easy";
	public static final String DIFFICULTY_2 = "medium";
	public static final String DIFFICULTY_3 = "hard";
    public static final List<String> DIFFICULTIES = List.of(DIFFICULTY_1, DIFFICULTY_2, DIFFICULTY_3);
	
	// Possible values for 'encoding':
	public static final String ENCODE_HTML = "";
	public static final String ENCODE_LEGACY = "urlLegacy";
	public static final String ENCODE_URL = "url3986";
	public static final String ENCODE_BASE64 = "base64";
    public static final List<String> ENCODINGS =  List.of(ENCODE_HTML, ENCODE_LEGACY, ENCODE_URL, ENCODE_BASE64);

}
