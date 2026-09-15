package com.github.pascalos99.quad_assignment_backend.trivia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories.Category;
import com.github.pascalos99.quad_assignment_backend.utils.FileUtils;
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionAndAnswer;

public class TriviaClientTests {
	
	private MockRestServiceServer mockServer;
	private TriviaClient triviaClient;

	private static final String EMPTY_RESPONSE =
            FileUtils.getResourceContent("/fixtures/trivia/questions-empty.json");

	record GetQuestionsUriCase(
			int amount, Integer category, String difficulty,
			String type, String encode, String token, String uri) {}
	
	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost");
		mockServer = MockRestServiceServer.bindTo(builder).build();
		triviaClient = new TriviaClient(builder);
	}
	
	@ParameterizedTest(name = "{index}")
	@MethodSource("getQuestionsUriCases")
	void getQuestions_generatesUri(GetQuestionsUriCase tc) {
		mockServer.expect(requestTo(tc.uri())).andRespond(withSuccess(EMPTY_RESPONSE, MediaType.APPLICATION_JSON));
		triviaClient.getQuestions(tc.amount(), tc.category(), tc.difficulty(), tc.type(), tc.encode(), tc.token());
		mockServer.verify();
	}
	
	@Test
	void getQuestions_deserializesSuccessResponse() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/questions-success.json");
		mockServer.expect(requestTo("http://localhost/api.php?amount=2"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		ApiResponse response = triviaClient.getQuestions(QuestionRequest.builder(2));
		assertThat(response.response_code()).isZero();
		assertThat(response.results()).containsExactly(
				new QuestionAndAnswer("boolean", "easy", "Science &amp; Nature",
						"Psychology is the science of behavior and mind.", "True", List.of("False")),
				new QuestionAndAnswer("multiple", "hard", "Entertainment: Video Games",
						"How many people can you recruit in the game Suikoden in a single playthrough?",
						"107", List.of("108","93","96"))
		);
	}
	
	@Test
	void getQuestions_deserializesFailureResponse() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/questions-failure.json");
		mockServer.expect(requestTo("http://localhost/api.php?amount=2"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		ApiResponse response = triviaClient.getQuestions(QuestionRequest.builder(2));
		assertThat(response.response_code()).isOne();
		assertThat(response.results()).isEmpty();
	}

    @Test
    void getQuestions_handlesRateLimitWithBody() {
        // Test if getQuestions handles rate-limit with body json present:
        String json1 = FileUtils.getResourceContent("/fixtures/trivia/questions-rate.json");
        mockServer.expect(requestTo("http://localhost/api.php?amount=2"))
                .andRespond(withTooManyRequests().body(json1).contentType(MediaType.APPLICATION_JSON));
        ApiResponse response1 = triviaClient.getQuestions(QuestionRequest.builder(2));
        assertThat(response1.response_code()).isEqualTo(5);
        assertThat(response1.results()).isEmpty();
    }

    @Test
    void getQuestions_handlesRateLimitWithoutBody() {
        // Test if getQuestions handles rate-limit without body json present:
        mockServer.expect(requestTo("http://localhost/api.php?amount=2"))
                .andRespond(withTooManyRequests());
        ApiResponse response2 = triviaClient.getQuestions(QuestionRequest.builder(2));
        assertThat(response2.response_code()).isEqualTo(5);
        assertThat(response2.results()).isEmpty();
    }
	
	@Test
	void getCategories_generatesUriAndDeserializes() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/categories.json");
		mockServer.expect(requestTo("http://localhost/api_category.php"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		Categories response = triviaClient.getCategories();
		assertThat(response.trivia_categories()).containsExactly(
				new Category(9, "General Knowledge"),
				new Category(13, "Entertainment: Musicals & Theatres"),
				new Category(32, "Entertainment: Cartoon & Animations")
		);
		mockServer.verify();
	}
	
	@Test
	void getToken_generatesUriAndDeserializesSuccess() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/token-request.json");
		mockServer.expect(requestTo("http://localhost/api_token.php?command=request"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		String token = triviaClient.getToken();
		assertThat(token).isEqualTo("t0k3n");
		mockServer.verify();
	}
	
	@Test
	void getToken_generatesUriAndDeserializesFailure() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/token-invalid.json");
		mockServer.expect(requestTo("http://localhost/api_token.php?command=request"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		String token = triviaClient.getToken();
		assertThat(token).isNull();
		mockServer.verify();
	}
	
	@Test
	void resetToken_generatesUriAndDeserializesSuccess() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/token-reset.json");
		mockServer.expect(requestTo("http://localhost/api_token.php?command=reset&token=t0k3n"))
						.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		boolean isValid = triviaClient.resetToken("t0k3n");
		assertThat(isValid).isTrue();
		mockServer.verify();
	}
	
	@Test
	void resetToken_generatesUriAndDeserializesFailure() {
		String json = FileUtils.getResourceContent("/fixtures/trivia/token-invalid.json");
		mockServer.expect(requestTo("http://localhost/api_token.php?command=reset&token=t0k3n"))
						.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		boolean isValid = triviaClient.resetToken("t0k3n");
		assertThat(isValid).isFalse();
		mockServer.verify();
	}
	
	static Stream<GetQuestionsUriCase> getQuestionsUriCases() {
		return Stream.of(
			new GetQuestionsUriCase(10, null, null  , null     , null    , null   , "http://localhost/api.php?amount=10"),
			new GetQuestionsUriCase(10, 10  , null  , null     , null    , null   , "http://localhost/api.php?amount=10&category=10"),
			new GetQuestionsUriCase(10, null, "easy", null     , null    , null   , "http://localhost/api.php?amount=10&difficulty=easy"),
			new GetQuestionsUriCase(10, 10  , "easy", null     , null    , null   , "http://localhost/api.php?amount=10&category=10&difficulty=easy"),
			
			new GetQuestionsUriCase(10, null, null  , "boolean", null    , null   , "http://localhost/api.php?amount=10&type=boolean"),
			new GetQuestionsUriCase(10, 10  , null  , "boolean", null    , null   , "http://localhost/api.php?amount=10&category=10&type=boolean"),
			new GetQuestionsUriCase(10, null, "easy", "boolean", null    , null   , "http://localhost/api.php?amount=10&difficulty=easy&type=boolean"),
			new GetQuestionsUriCase(10, 10  , "easy", "boolean", null    , null   , "http://localhost/api.php?amount=10&category=10&difficulty=easy&type=boolean"),
			
			new GetQuestionsUriCase(10, null, null  , null     , "base64", null   , "http://localhost/api.php?amount=10&encode=base64"),
			new GetQuestionsUriCase(10, 10  , null  , null     , "base64", null   , "http://localhost/api.php?amount=10&category=10&encode=base64"),
			new GetQuestionsUriCase(10, null, "easy", null     , "base64", null   , "http://localhost/api.php?amount=10&difficulty=easy&encode=base64"),
			new GetQuestionsUriCase(10, 10  , "easy", null     , "base64", null   , "http://localhost/api.php?amount=10&category=10&difficulty=easy&encode=base64"),
			
			new GetQuestionsUriCase(10, null, null  , "boolean", "base64", null   , "http://localhost/api.php?amount=10&type=boolean&encode=base64"),
			new GetQuestionsUriCase(10, 10  , null  , "boolean", "base64", null   , "http://localhost/api.php?amount=10&category=10&type=boolean&encode=base64"),
			new GetQuestionsUriCase(10, null, "easy", "boolean", "base64", null   , "http://localhost/api.php?amount=10&difficulty=easy&type=boolean&encode=base64"),
			new GetQuestionsUriCase(10, 10  , "easy", "boolean", "base64", null   , "http://localhost/api.php?amount=10&category=10&difficulty=easy&type=boolean&encode=base64"),
			
			
			new GetQuestionsUriCase(10, null, null  , null     , null    , "t0k3n", "http://localhost/api.php?amount=10&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , null  , null     , null    , "t0k3n", "http://localhost/api.php?amount=10&category=10&token=t0k3n"),
			new GetQuestionsUriCase(10, null, "easy", null     , null    , "t0k3n", "http://localhost/api.php?amount=10&difficulty=easy&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , "easy", null     , null    , "t0k3n", "http://localhost/api.php?amount=10&category=10&difficulty=easy&token=t0k3n"),
			
			new GetQuestionsUriCase(10, null, null  , "boolean", null    , "t0k3n", "http://localhost/api.php?amount=10&type=boolean&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , null  , "boolean", null    , "t0k3n", "http://localhost/api.php?amount=10&category=10&type=boolean&token=t0k3n"),
			new GetQuestionsUriCase(10, null, "easy", "boolean", null    , "t0k3n", "http://localhost/api.php?amount=10&difficulty=easy&type=boolean&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , "easy", "boolean", null    , "t0k3n", "http://localhost/api.php?amount=10&category=10&difficulty=easy&type=boolean&token=t0k3n"),
			
			new GetQuestionsUriCase(10, null, null  , null     , "base64", "t0k3n", "http://localhost/api.php?amount=10&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , null  , null     , "base64", "t0k3n", "http://localhost/api.php?amount=10&category=10&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, null, "easy", null     , "base64", "t0k3n", "http://localhost/api.php?amount=10&difficulty=easy&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , "easy", null     , "base64", "t0k3n", "http://localhost/api.php?amount=10&category=10&difficulty=easy&encode=base64&token=t0k3n"),
			
			new GetQuestionsUriCase(10, null, null  , "boolean", "base64", "t0k3n", "http://localhost/api.php?amount=10&type=boolean&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , null  , "boolean", "base64", "t0k3n", "http://localhost/api.php?amount=10&category=10&type=boolean&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, null, "easy", "boolean", "base64", "t0k3n", "http://localhost/api.php?amount=10&difficulty=easy&type=boolean&encode=base64&token=t0k3n"),
			new GetQuestionsUriCase(10, 10  , "easy", "boolean", "base64", "t0k3n", "http://localhost/api.php?amount=10&category=10&difficulty=easy&type=boolean&encode=base64&token=t0k3n")
		);
	}
	
}
