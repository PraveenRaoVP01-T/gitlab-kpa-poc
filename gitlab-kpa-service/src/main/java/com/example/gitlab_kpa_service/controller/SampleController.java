package com.example.gitlab_kpa_service.controller;

import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sample")
public class SampleController {
    private final ProjectService projectService;

    public SampleController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<Flux<GitLabProjectsDTO>> getProjects(
    ) {
        return ResponseEntity.ok(projectService.getProjectsForUser());
    }
}
