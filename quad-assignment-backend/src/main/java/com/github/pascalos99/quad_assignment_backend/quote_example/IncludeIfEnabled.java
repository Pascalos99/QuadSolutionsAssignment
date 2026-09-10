package com.github.pascalos99.quad_assignment_backend.quote_example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(
	    name = "feature.quote.enabled",
	    havingValue = "true",
	    matchIfMissing = false
)
public @interface IncludeIfEnabled {}
