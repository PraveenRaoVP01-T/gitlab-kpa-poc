package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.Projects;
import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.model.IssuesDTO;
import com.example.gitlab_kpa_service.model.MergeRequestsDTO;
import com.example.gitlab_kpa_service.utils.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    public Flux<MergeRequestsDTO> getMergeRequestsForProject(Long projectId) {
        String endpoint = ApiEndpoints.GET_ALL_MERGE_REQUESTS_BY_PROJECT_ID.formatted(projectId.toString());
        Flux<MergeRequestsDTO> mergeRequestsForProject = apiClient.getFlux(endpoint, MergeRequestsDTO.class);
        return mergeRequestsForProject;
    }

    public Flux<IssuesDTO> getIssuesForProject(Long projectId) {
        String endpoint = ApiEndpoints.GET_ISSUES_BY_PROJECT_ID.formatted(projectId.toString());
        Flux<IssuesDTO> issuesForProject = apiClient.getFlux(endpoint, IssuesDTO.class);
        return issuesForProject;
    }

    public Flux<MergeRequestsDTO> getMergeRequestsForIssues(Long projectId, Long issueIID) {
        String endpoint = ApiEndpoints.GET_RELATED_MR_BY_ISSUE_IID.formatted(projectId.toString(), issueIID.toString());
        Flux<MergeRequestsDTO> mergeRequestsForIssue = apiClient.getFlux(endpoint, MergeRequestsDTO.class);
        return mergeRequestsForIssue;
    }

}
