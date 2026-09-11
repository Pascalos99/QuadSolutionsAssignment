package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionAndAnswer(
		String type,
		String difficulty,
		String category,
		String question,
		String correct_answer,
		List<String> incorrect_answers
) {}
