package com.github.pascalos99.quad_assignment_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class QuadAssignmentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuadAssignmentBackendApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(defaultValue = "World!") String name, @RequestParam(name = "number", defaultValue = "1") int count) {
		return String.format("(%d) Hello %s", count, name);
	}

}
