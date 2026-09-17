package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

import com.github.pascalos99.quad_assignment_backend.trivia.model.Categories.Category;

public record ApiInfo(String msg, List<Category> categories, List<String> types, List<String> difficulties, List<String> encodes) {}
