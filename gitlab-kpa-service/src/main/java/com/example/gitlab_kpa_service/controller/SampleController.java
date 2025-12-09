package com.example.gitlab_kpa_service.controller;

import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.service.DeveloperKpiService;
import com.example.gitlab_kpa_service.service.GitlabService;
import com.example.gitlab_kpa_service.service.ProjectService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/sample")
public class SampleController {
    private final ProjectService projectService;
    private final GitlabService gitlabService;
    private final DeveloperKpiService developerKpiService;

    public SampleController(ProjectService projectService, GitlabService gitlabService, DeveloperKpiService developerKpiService) {
        this.projectService = projectService;
        this.gitlabService = gitlabService;
        this.developerKpiService = developerKpiService;
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

    @PostMapping("/save/all")
    public ResponseEntity<AllData> saveAllData() {
        return ResponseEntity.ok(gitlabService.getAllData());
    }

    @GetMapping("/projects/kpi")
    public ResponseEntity<List<ProjectData>> getContributorData(
    ) {
        return ResponseEntity.ok(developerKpiService.getBasicDeveloperKpis());
    }

    @GetMapping("/developer/kpi/today")
    public ResponseEntity<List<ProjectData>> getContributorDataByUserName(
            @RequestParam String username,
            @RequestParam(required = false) LocalDate date
            ) {
        return ResponseEntity.ok(developerKpiService.getTodayDeveloperKpiByUsername(username, date));
    }
}
