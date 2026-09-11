package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Categories(List<Category> trivia_categories) {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static record Category(int id, String name) {}
}
