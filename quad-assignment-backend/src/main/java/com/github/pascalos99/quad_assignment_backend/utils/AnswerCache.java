package com.github.pascalos99.quad_assignment_backend.utils;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class AnswerCache {
	
	private Cache<UUID, String> store;
	
	public AnswerCache(
			@Value("${trivia.feature.ttl}") Duration TTL,
			@Value("${trivia.feature.cache-limit}") Long maxSize
	) {
		store = Caffeine.newBuilder()
				.expireAfterWrite(TTL)
				.maximumSize(maxSize)
				.build();
	}
	
	/**
	 * @param value The value to store
	 * @return The unique key of the added value
	 */
	public UUID put(String value) {
		UUID key = UUID.randomUUID();
		store.put(key, value);
		return key;
	}
	
	public String remove(UUID key) {
		String value = store.getIfPresent(key);
		store.invalidate(key);
		return value;
	}
	
}
