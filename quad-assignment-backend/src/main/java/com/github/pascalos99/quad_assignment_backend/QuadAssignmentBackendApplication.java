package com.github.pascalos99.quad_assignment_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This example uses the following
 * <a href=https://github.com/spring-guides/gs-consuming-rest.git>repository</a>
 * to run a "quote API" at {@code localhost:8080/api}.
 * <p>
 * This code is based on a <a href=https://spring.io/guides/gs/consuming-rest>guide</a>
 * from {@code spring.io} and loosely modified to work as an in-between API.
 */
@SpringBootApplication
public class QuadAssignmentBackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(QuadAssignmentBackendApplication.class, args);
	}

}
