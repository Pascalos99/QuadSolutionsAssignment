package com.github.pascalos99.quad_assignment_backend.trivia;

import com.github.pascalos99.quad_assignment_backend.RestClientConfig;
import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = { RestClientConfig.class, TriviaClient.class })
public class TriviaClientLiveTests {

    @Autowired
    private TriviaClient triviaClient;

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void resetToken_detectsValidAndInvalidToken() {
        assertThat(triviaClient.resetToken("invalid-token")).isFalse();
        String token = triviaClient.getToken();
        assertThat(token).isNotNull();
        assertThat(triviaClient.resetToken(token)).isTrue();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void getCategories_returnsWellFormedResponse() {
        Categories categories = triviaClient.getCategories();
        assertThat(categories).isNotNull();
        assertThat(categories.trivia_categories()).isNotEmpty();
        assertThat(categories.trivia_categories()).allSatisfy(cat -> {
            assertThat(cat.id()).isGreaterThan(0);
            assertThat(cat.name()).isNotBlank();
        });
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void getQuestions_returnsWellFormedResponse() {
        int numberOfQuestions = 10;
        String token = triviaClient.getToken();
        QuestionRequest request = QuestionRequest.builder(numberOfQuestions).setToken(token).build();
        ApiResponse response = repeatGetQuestionsUntilPresent(request,
                // Timeout:
                Duration.ofSeconds(10),
                // Poll-interval:
                Duration.ofSeconds(2)
        );

        // As per the OpenTDB API contract:
        /*
         * If 50 or fewer questions are requested, and
         * the response-code is 0, then the result will
         * contain exactly the number of questions requested.
         */
        assertThat(response.results()).hasSize(numberOfQuestions);

        // We cannot assert anything about the exact content of each question
        // But we can assert that the content must be present.
        assertThat(response.results()).allSatisfy(
                qna ->
        {
            assertThat(qna.category()).isNotBlank();
            assertThat(qna.question()).isNotBlank();
            assertThat(qna.difficulty()).isNotBlank();
            assertThat(qna.correct_answer()).isNotBlank();
            assertThat(qna.incorrect_answers()).isNotEmpty();
            assertThat(qna.incorrect_answers()).allSatisfy(ans -> {
                assertThat(ans).isNotBlank();
            });
            // We make only one assertion about content:
            assertThat(qna.type()).isIn(TriviaClient.TYPES);
            /*
             * The types of questions that may be returned
             * should be of the types that our code knows how
             * to handle. A new type being added to the API
             * contract should warrant a code update and
             * should thus be flagged by the test.
             */
        });
    }

    ApiResponse repeatGetQuestionsUntilPresent(QuestionRequest request, Duration timeout, Duration pollInterval) {
        AtomicReference<ApiResponse> reference = new AtomicReference<>();
        await()
                .atMost(timeout)
                .pollInterval(pollInterval)
                .pollDelay(Duration.ZERO)
                .untilAsserted(() ->
                {
                    ApiResponse response = triviaClient.getQuestions(request);
                    if (response == null)
                        throw new IllegalStateException("response should not be null");
                    assertThat(response.response_code()).isZero();
                    reference.set(response);
                });
        return reference.get();
    }

}
