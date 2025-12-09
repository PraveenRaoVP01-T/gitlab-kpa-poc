package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.utils.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<CommitListDTO> getCommitsByMergeRequest(Long projectId, Long mergeRequestIid) {
        String endpoint = ApiEndpoints.GET_COMMITS_BY_MERGE_REQUEST_IID.formatted(projectId.toString(), mergeRequestIid.toString());
        Flux<CommitListDTO> commits = apiClient.getFlux(endpoint, CommitListDTO.class);
        return commits;
    }

    public Mono<CommitDTO> getCommitDataById(Long projectId, String commitSha) {
        String endpoint = ApiEndpoints.GET_COMMIT_DATA_BY_ID.formatted(projectId.toString(), commitSha);
        Mono<CommitDTO> commit = apiClient.get(endpoint, CommitDTO.class);
        return commit;
    }

}
