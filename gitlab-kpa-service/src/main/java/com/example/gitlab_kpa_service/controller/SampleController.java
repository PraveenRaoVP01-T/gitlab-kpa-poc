package com.example.gitlab_kpa_service.controller;

import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.model.IssuesDTO;
import com.example.gitlab_kpa_service.model.MergeRequestsDTO;
import com.example.gitlab_kpa_service.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/sample")
public class SampleController {
    private final ProjectService projectService;

    public SampleController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public ResponseEntity<Flux<GitLabProjectsDTO>> getProjects(
    ) {
        return ResponseEntity.ok(projectService.getProjectsForUser());
    }

    @GetMapping("/mr/{projectId}")
    public ResponseEntity<Flux<MergeRequestsDTO>> getMergeRequestsForProject(
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(projectService.getMergeRequestsForProject(projectId));
    }

    @GetMapping("/issues/{projectId}")
    public ResponseEntity<Flux<IssuesDTO>> getIssuesForProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getIssuesForProject(projectId));
    }
}
