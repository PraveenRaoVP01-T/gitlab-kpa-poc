package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.*;
import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.model.IssuesDTO;
import com.example.gitlab_kpa_service.model.MergeRequestsDTO;
import com.example.gitlab_kpa_service.repository.IssueMergeRequestLinkRepository;
import com.example.gitlab_kpa_service.repository.IssuesRepository;
import com.example.gitlab_kpa_service.repository.MergeRequestsRepository;
import com.example.gitlab_kpa_service.repository.ProjectsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitlabService {
    private final ProjectService projectService;
    private final ProjectsRepository projectsRepository;

    private final MergeRequestsRepository mergeRequestsRepository;

    private final IssuesRepository issuesRepository;

    private final IssueMergeRequestLinkRepository issueMergeRequestLinkRepository;


    private List<Projects> saveProjectsToDb() {
        Flux<GitLabProjectsDTO> projectsFlux = projectService.getProjectsForUser();
        List<Projects> projectsList = projectsFlux.toStream().map(
                project -> Projects.builder()
                        .gitlabProjectId(project.getId())
                        .name(project.getName())
                        .pathWithNamespace(project.getPathWithNamespace())
                        .createdAt(project.getCreatedAt())
                        .updatedAt(project.getUpdatedAt())
                        .build()
        ).toList();

        return projectsRepository.saveAll(projectsList);
    }

    private List<MergeRequests> saveMergeRequestsToDb(List<Projects> projects) {
        List<MergeRequests> mergeRequests = new ArrayList<>();
        for(Projects project : projects) {
            Flux<MergeRequestsDTO> mergeRequestsDTOFlux = projectService.getMergeRequestsForProject(project.getGitlabProjectId());
            List<MergeRequests> mergeRequestsToSave = mergeRequestsDTOFlux.toStream()
                    .map(mr -> MergeRequests.builder()
                            .gitlabMrId(mr.getId())
                            .gitlabMrIID(mr.getIid())
                            .project(project)
                            .sourceBranch(mr.getSourceBranch())
                            .targetBranch(mr.getTargetBranch())
                            .title(mr.getTitle())
                            .description(mr.getDescription())
                            .state(mr.getState())
                            .authorUsername(mr.getAuthor().getUsername())
                            .issueType(null)
                            .createdAt(mr.getCreatedAt())
                            .updatedAt(mr.getUpdatedAt())
                            .mergedAt(mr.getMergedAt())
                            .closedAt(mr.getClosedAt())
                            .build()).toList();

            mergeRequests.addAll(mergeRequestsToSave);
        }
        return mergeRequestsRepository.saveAll(mergeRequests);
    }

    private List<Issues> saveIssuesToDb(List<Projects> projects) {
        List<Issues> issues = new ArrayList<>();
        for(Projects project : projects) {
            Flux<IssuesDTO> issuesDTOFlux = projectService.getIssuesForProject(project.getGitlabProjectId());
            List<Issues> issuesToSave = issuesDTOFlux.toStream()
                    .map(issue -> Issues.builder()
                            .gitlabIssueId(issue.getId())
                            .gitlabIssueIID(issue.getIid())
                            .project(project)
                            .title(issue.getTitle())
                            .description(issue.getDescription())
                            .state(issue.getState())
                            .issueType(issue.getIssueType())
                            .labels(issue.getLabels())
                            .severity(issue.getSeverity())
                            .createdAt(issue.getCreatedAt())
                            .updatedAt(issue.getUpdatedAt())
                            .closedAt(issue.getClosedAt())
                            .build()).toList();

            issues.addAll(issuesToSave);
        }

        return issuesRepository.saveAll(issues);
    }

    private List<IssueMergeRequestLink> saveIssueMergeRequests(List<Issues> issues) {
        List<IssueMergeRequestLink> issueMergeRequestLinks = new ArrayList<>();

        for(Issues issue : issues) {
            List<MergeRequests> mergeRequestsForProject = mergeRequestsRepository.findByProject(issue.getProject());
            Flux<MergeRequestsDTO> mergeRequestsForIssue = projectService.getMergeRequestsForIssues(issue.getProject().getGitlabProjectId(), issue.getGitlabIssueIID());
            List<MergeRequests> filteredMergeRequests = mergeRequestsForProject
                    .stream()
                    .filter(mr ->mergeRequestsForIssue.toStream().anyMatch(mri -> mri.getId().equals(mr.getGitlabMrId())))
                    .toList();

            filteredMergeRequests = filteredMergeRequests.stream().peek(mr -> mr.setIssueType(issue.getIssueType())).toList();
            filteredMergeRequests = mergeRequestsRepository.saveAll(filteredMergeRequests);

            List<IssueMergeRequestLink> issueMergeRequestLinksToSave = filteredMergeRequests
                    .stream()
                    .map(mr -> {
                        IssueMRLinkId issueMRLinkId = new IssueMRLinkId(issue.getId(), mr.getId());
                        return IssueMergeRequestLink.builder()
                                .id(issueMRLinkId)
                                .issue(issue)
                                .mergeRequest(mr)
                                .build();
                    }).toList();

            issueMergeRequestLinks.addAll(issueMergeRequestLinksToSave);
        }

        return issueMergeRequestLinkRepository.saveAll(issueMergeRequestLinks);
    }
}
