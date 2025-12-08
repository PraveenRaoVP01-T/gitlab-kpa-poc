package com.example.gitlab_kpa_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitlabWebClientConfig {
    @Value("${gitlab.username}")
    private String username;

    @Value("${gitlab.base-url}")
    private String baseUrl;

    @Value("${gitlab.secret-token}")
    private String token;

    @Bean
    public WebClient gitlabWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("PRIVATE-TOKEN", token)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
