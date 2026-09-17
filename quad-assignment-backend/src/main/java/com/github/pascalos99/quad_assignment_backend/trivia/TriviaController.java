package com.github.pascalos99.quad_assignment_backend.trivia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.github.pascalos99.quad_assignment_backend.trivia.model.Answer;
import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiInfo;
import com.github.pascalos99.quad_assignment_backend.trivia.model.ApiResponse;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories;
import com.github.pascalos99.quad_assignment_backend.trivia.model.Question;
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionAndAnswer;
import com.github.pascalos99.quad_assignment_backend.trivia.model.QuestionsAndToken;
import com.github.pascalos99.quad_assignment_backend.utils.AnswerCache;

@RestController
@RequestMapping("")
public class TriviaController {
	
	private final TriviaClient triviaClient;
	
	private AnswerCache questionsAndAnswers;
	
	public TriviaController(TriviaClient triviaClient, AnswerCache dataStore) {
		this.triviaClient = triviaClient;
		questionsAndAnswers = dataStore;
	}
	
	@GetMapping("/help")
	public ApiInfo getInfo() {
		String msg =
				"""
				Usage for GET /questions:
				
				GET /questions[?amount=<INT>][&category=<INT>][&type=<STR>]\
				[&difficulty=<STR>][&encode=<STR>][&token=<STR>]
				
				<> placeholder type (STR = string, INT = integer)
				[] optional parameter
				Parameters without brackets are required.
				
				When not included, the 'amount' defaults to 10.
				
				See 'categories', 'types', 'difficulties', and 'encodes' for \
				a list of allowed values for the corresponding parameters.
				For 'token', only tokens returned by previous API calls are valid.
				""";
		Categories categories = triviaClient.getCategories();
		return new ApiInfo(
				msg, 
				categories.trivia_categories(),
				TriviaClient.TYPES,
				TriviaClient.DIFFICULTIES,
				TriviaClient.ENCODINGS
		);
	}
	
	@GetMapping("/questions")
	public QuestionsAndToken getQuestions(
			@RequestParam(defaultValue = "10") int count,
			@RequestParam(required = false) Integer category,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String difficulty,
			@RequestParam(required = false) String encode,
			@RequestParam(required = false) String token
		) {
		if (count <= 0) throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Invalid number of questions requested ("+count+")");
		
		String usingToken = token == null ? triviaClient.getToken() : token;
		
		QuestionRequest.Builder qr = QuestionRequest.builder(count);
		// Note that excluded parameters are passed as 'null', which
		//  simply excludes them from the final API call, thanks to
		//  the TriviaClient class.
		qr.setCategory(category);
		qr.setType(type);
		qr.setDifficulty(difficulty);
		qr.setEncode(encode);
		// if no valid token was generated, it will simply be 'null'
		//  and thus be ignored and not break anything major.
		qr.setToken(usingToken);
		
		/*
		 * We wait until there is a spot available to access the API.
		 * This is a *BLOCKING* operation.
		 * As such, GET requests made to '/questions' will *STALL*
		 *  until a spot is available in the external API.
		 * This behaviour is not always desirable, but was in
		 *  this case a conscious choice.
		 */
		triviaClient.waitForApiTimeoutEnd();
		
		ApiResponse resp = triviaClient.getQuestions(qr);
		List<Question> result = new ArrayList<>();
		for (QuestionAndAnswer qna : resp.results()) {
			UUID uuid = questionsAndAnswers.put(qna.correct_answer());
			List<String> answers = qna.incorrect_answers();
			answers.add(qna.correct_answer());
			if (qna.type().contentEquals(TriviaClient.TYPE_BOOL)) {
				Collections.sort(answers, Collections.reverseOrder());
			} else {
				Collections.shuffle(answers);
			}
			result.add(new Question(
					uuid, qna.type(), qna.difficulty(),
					qna.category(), qna.question(), answers));
		}
		return new QuestionsAndToken(usingToken, result);
	}
	
	/**
	 * This mapping checks the truth-value of one or several answers to questions.
	 * A question-session is uniquely identified by its id. After calling /checkanswers
	 * on an id, the question-session is removed from the server, so that it may not be 
	 * queried again.
	 * @param answer the answer to the question, given by the user
	 * @return {@code true} if the answer is correct, or {@code false} if it is false.
	 * Returns {@code null} if the question-id is invalid or no longer valid.
	 */
	@PostMapping("/checkanswers")
	public Boolean postAnswers(@RequestBody Answer answer) {
		String trueAnswer = questionsAndAnswers.remove(answer.uuid());
		if (trueAnswer == null) {
			throw new ResponseStatusException(HttpStatus.GONE,
					"Unknown or expired question-uuid %s".formatted(answer.uuid()));
		}
		return trueAnswer.contentEquals(answer.answer());
	}
	
}
