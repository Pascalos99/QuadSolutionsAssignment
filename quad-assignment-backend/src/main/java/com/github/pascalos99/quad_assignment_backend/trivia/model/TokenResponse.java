package com.github.pascalos99.quad_assignment_backend.trivia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponse(int response_code, String token) {}
