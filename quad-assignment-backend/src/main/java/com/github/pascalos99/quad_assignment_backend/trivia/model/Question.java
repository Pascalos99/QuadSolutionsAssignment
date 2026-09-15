package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;
import java.util.UUID;

public record Question(
		UUID uuid,
		String type,
		String difficulty,
		String category,
		String question,
		List<String> answers
) {}
