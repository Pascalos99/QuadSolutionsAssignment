package com.github.pascalos99.quad_assignment_backend.trivia;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Question;
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionAndAnswer;

@RestController
@RequestMapping("")
public class TriviaController {
	
	private final TriviaClient triviaClient;
	
	public TriviaController(TriviaClient triviaClient) {
		this.triviaClient = triviaClient;
	}
	
	@GetMapping("/questions")
	public synchronized List<Question> getQuestions() {
		QuestionRequest.Builder qr = QuestionRequest.builder(10);
		
		/*
		 * The following is the only implementation really needed to make the
		 * rate-limit of the external API be handled properly.
		 * I noticed that every get-mapping seems to run on its own thread:
		 *    calling Thread.sleep on one get-mapping did not stall any
		 *    of the other get-mappings. Additionally, the get mapping
		 *    is set as a 'synchronized' method, such that only
		 *    one thread enters it at any given time. So Thread.sleep makes all
		 *    the requests wait, not just the current one. This is ideal behaviour
		 *    for a rate-limited-queue.
		 * Therefore, all we need to do here is to wait at least until 5 seconds
		 * after the previous successful HTTP request to the OpenTDB API.
		 * This is handled inside the TriviaClient code, from which we can just
		 * retrieve the ApiTimeoutUntil getter and wait if the timeout is still active.
		 */
		Instant apiTimeoutUntil = triviaClient.getApiTimeoutUntil();
		Instant rightNow = Instant.now();
		if (apiTimeoutUntil.isAfter(rightNow)) {
			try {
				Thread.sleep(Duration.between(rightNow, apiTimeoutUntil));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ApiResponse resp = triviaClient.getQuestions(qr);
		List<Question> result = new ArrayList<>();
		for (QuestionAndAnswer qna : resp.results()) {
			List<String> answers = qna.incorrect_answers();
			answers.add(qna.correct_answer());
			Collections.shuffle(answers);
			result.add(new Question(
					qna.type(), qna.difficulty(), qna.category(),
					qna.question(), answers));
		}
		return result;
	}
	
}
