package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

public record QuestionsAndToken(String token, List<Question> questions) {}
