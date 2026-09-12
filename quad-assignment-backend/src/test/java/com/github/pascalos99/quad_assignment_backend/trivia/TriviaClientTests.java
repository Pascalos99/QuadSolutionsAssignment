package com.github.pascalos99.quad_assignment_backend.trivia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionAndAnswer;

public class TriviaClientTests {
	
	private MockRestServiceServer mockServer;
	private TriviaClient triviaClient;
	
	private static final String EMPTY_RESPONSE = "{\"response_code\":0,\"results\":[]}";
	
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
	void getQuestions_correctlyGeneratesUri(GetQuestionsUriCase tc) {
		mockServer.expect(requestTo(tc.uri())).andRespond(withSuccess(EMPTY_RESPONSE, MediaType.APPLICATION_JSON));
		triviaClient.getQuestions(tc.amount(), tc.category(), tc.difficulty(), tc.type(), tc.encode(), tc.token());
		mockServer.verify();
	}
	
	@Test
	void getQuestions_correctlyDeserializesSuccessResponse() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/questions-success.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api.php?amount=2")).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		ApiResponse response = triviaClient.getQuestions(2, null, null, null, null, null);
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
	void getQuestions_correctlyDeserializesFailureResponse() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/questions-failure.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api.php?amount=2")).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		ApiResponse response = triviaClient.getQuestions(2, null, null, null, null, null);
		assertThat(response.response_code()).isOne();
		assertThat(response.results()).isEmpty();
	}
	
	@Test
	void getCategories_correctlyGeneratesUriAndDeserializes() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/categories.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api_category.php")).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		Categories response = triviaClient.getCategories();
		assertThat(response.trivia_categories()).containsExactly(
				new Category(9, "General Knowledge"),
				new Category(13, "Entertainment: Musicals & Theatres"),
				new Category(32, "Entertainment: Cartoon & Animations")
		);
		mockServer.verify();
	}
	
	@Test
	void getToken_correctlyGeneratesUriAndDeserializesSuccess() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/token-request.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api_token.php?command=request")).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		String token = triviaClient.getToken();
		assertThat(token).isEqualTo("t0k3n");
		mockServer.verify();
	}
	
	@Test
	void getToken_correctlyGeneratesUriAndDeserializesFailure() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/token-invalid.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api_token.php?command=request")).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		String token = triviaClient.getToken();
		assertThat(token).isNull();
		mockServer.verify();
	}
	
	@Test
	void resetToken_correctlyGeneratesUriAndDeserializesSuccess() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/token-reset.json").readAllBytes());
		mockServer.expect(requestTo("http://localhost/api_token.php?command=reset&token=t0k3n"))
						.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
		boolean isValid = triviaClient.resetToken("t0k3n");
		assertThat(isValid).isTrue();
		mockServer.verify();
	}
	
	@Test
	void resetToken_correctlyGeneratesUriAndDeserializesFailure() throws IOException {
		String json = new String(getClass().getResourceAsStream("/fixtures/trivia/token-invalid.json").readAllBytes());
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
