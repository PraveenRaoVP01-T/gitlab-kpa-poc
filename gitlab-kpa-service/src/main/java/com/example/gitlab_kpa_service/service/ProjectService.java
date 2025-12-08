package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.utils.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    @Value("${gitlab.username}")
    private String userName;

    private final GitlabApiClient apiClient;

    public Flux<GitLabProjectsDTO> getProjectsForUser() {
        String endpoint = ApiEndpoints.GET_ALL_PROJECTS_FOR_USER.formatted(userName);
        Flux<GitLabProjectsDTO> projectsResponse = apiClient.getFlux(endpoint, GitLabProjectsDTO.class)
                .doOnNext(project ->
                    System.out.println("Project Id: " + project.getId())
                );
        return projectsResponse;
    }

    public Mono<GitLabProjectsDTO> getProjectById(String projectId) {
        String endpoint = ApiEndpoints.GET_PROJECT_BY_ID.formatted(projectId);
        Mono<GitLabProjectsDTO> projectData = apiClient.get(endpoint, GitLabProjectsDTO.class);
        return projectData;
    }
}
