package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

public record Question(
		String type,
		String difficulty,
		String category,
		String question,
		List<String> answers
) {}
