package com.github.pascalos99.quad_assignment_backend.trivia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Question;
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionAndAnswer;
import com.github.pascalos99.quad_assignment_backend.utils.SynchronizedWaitingQueue;

@RestController
@RequestMapping("")
public class TriviaController {
	
	private final TriviaClient triviaClient;
	private final SynchronizedWaitingQueue queue;
	
	public TriviaController(TriviaClient triviaClient, SynchronizedWaitingQueue queue) {
		this.triviaClient = triviaClient;
		this.queue = queue;
	}
	
	@GetMapping("/questions")
	public synchronized List<Question> getQuestions() {
		QuestionRequest.Builder qr = QuestionRequest.builder(10);
		
		/*
		 * We wait until there is a spot available to access the API.
		 * This is a *BLOCKING* operation.
		 * As such, GET requests made to '/questions' will *STALL*
		 *  until a spot is available in the external API.
		 * This behaviour is not always desirable, but was in
		 *  this case a conscious choice.
		 */
		queue.waitUntil(triviaClient.getApiTimeoutUntil());
		
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
