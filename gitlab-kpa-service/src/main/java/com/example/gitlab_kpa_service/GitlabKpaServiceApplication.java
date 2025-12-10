package com.example.gitlab_kpa_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GitlabKpaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitlabKpaServiceApplication.class, args);
	}

}
