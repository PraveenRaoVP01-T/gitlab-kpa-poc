package com.example.gitlab_kpa_service.controller;

import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.service.*;
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
    private final GitlabCronJobs cronJobsService;


    public SampleController(ProjectService projectService, GitlabService gitlabService, DeveloperKpiService developerKpiService, GitlabCronJobs cronJobsService) {
        this.projectService = projectService;
        this.gitlabService = gitlabService;
        this.developerKpiService = developerKpiService;
        this.cronJobsService = cronJobsService;
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

    @PostMapping("/sync")
    public ResponseEntity<AllData> syncData(
            @RequestParam(required = false) LocalDate date
    ) {
        return ResponseEntity.ok(cronJobsService.getAllData(date));
    }
}
