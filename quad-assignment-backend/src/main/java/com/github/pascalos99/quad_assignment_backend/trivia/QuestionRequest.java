package com.github.pascalos99.quad_assignment_backend.trivia;

import org.jspecify.annotations.Nullable;

public record QuestionRequest(
        int amount, @Nullable Integer category, @Nullable String difficulty,
        @Nullable String type, @Nullable String encode, @Nullable String token) {

    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(int amount) {
        return new Builder(amount);
    }

    public static class Builder {
        private int amount;
        private Integer category;
        private String difficulty;
        private String type;
        private String encode;
        private String token;

        public Builder() {
            this(1);
        }
        public Builder(int amount) {
            this.amount = amount;
        }

        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }
        public Builder setCategory(int category) {
            this.category = category;
            return this;
        }
        public Builder setDifficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }
        public Builder setType(String type) {
            this.type = type;
            return this;
        }
        public Builder setEncode(String encode) {
            this.encode = encode;
            return this;
        }
        public Builder setToken(String token) {
            this.token = token;
            return this;
        }
        public QuestionRequest build() {
            return new QuestionRequest(amount, category, difficulty, type, encode, token);
        }
    }

}