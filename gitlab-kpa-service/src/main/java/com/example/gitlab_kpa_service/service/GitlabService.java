package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.*;
import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GitlabService {
    private final ProjectService projectService;
    private final ProjectsRepository projectsRepository;

    private final MergeRequestsRepository mergeRequestsRepository;

    private final IssuesRepository issuesRepository;

    private final IssueMergeRequestLinkRepository issueMergeRequestLinkRepository;

    private final CommitRepository commitRepository;


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

    @Transactional
    private List<IssueMergeRequestLink> saveIssueMergeRequests(List<Issues> issues) {
        List<IssueMergeRequestLink> issueMergeRequestLinks = new ArrayList<>();

        for(Issues issue : issues) {
            List<MergeRequests> mergeRequestsForProject = mergeRequestsRepository.findByProject(issue.getProject());
            Flux<MergeRequestsDTO> mergeRequestsForIssue = projectService.getMergeRequestsForIssues(issue.getProject().getGitlabProjectId(), issue.getGitlabIssueIID());
            List<MergeRequests> filteredMergeRequests = mergeRequestsForProject
                    .stream()
                    .filter(mr -> mergeRequestsForIssue.toStream().anyMatch(mri -> mri.getId().equals(mr.getGitlabMrId())))
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

    @Transactional
    private List<Commit> saveCommits(List<MergeRequests> mergeRequests) {
        List<Commit> commits = new ArrayList<>();
        List<CommitFile> commitFiles = new ArrayList<>();
        List<CommitStats> commitStats = new ArrayList<>();

        for(MergeRequests request: mergeRequests) {
            List<Commit> commitInDb = commitRepository.findByMergeRequest(request);
            Flux<CommitListDTO> commitDTOs = projectService.getCommitsByMergeRequest(request.getProject().getGitlabProjectId(), request.getGitlabMrIID());
            List<CommitListDTO> newCommits = commitDTOs.toStream()
                    .filter(commitDto -> commitInDb.stream().noneMatch(c -> c.getGitlabCommitId().equals(commitDto.getId())))
                    .toList();

            List<Commit> commitsToSave = newCommits.stream()
                    .map(commit -> Commit.builder()
                            .gitlabCommitId(commit.getId())
                            .mergeRequest(request)
                            .project(request.getProject())
                            .authorName(commit.getAuthorName())
                            .authorEmail(commit.getAuthorEmail())
                            .committedAt(commit.getCommittedAt())
                            .title(commit.getTitle())
                            .message(commit.getMessage())
                            .build()).toList();

            commits.addAll(commitsToSave);
            List<CommitStats> commitStatistics = commitsToSave.stream().map(
                    commit -> {
                        CommitDTO commitDTO = projectService.getCommitDataById(request.getProject().getGitlabProjectId(), commit.getGitlabCommitId()).block();
                        if(commitDTO != null) {
                            return CommitStats.builder()
                                    .commit(commit)
                                    .linesAdded(commitDTO.getStats().getAdditions())
                                    .linesDeleted(commitDTO.getStats().getDeletions())
                                    .netNewLines(commitDTO.getStats().getAdditions() - commitDTO.getStats().getDeletions())
                                    .build();
                        }
                        return null;
                    }
            ).filter(Objects::nonNull).toList();

            commitStats.addAll(commitStatistics);
        }

        commits = commitRepository.saveAll(commits);

        return commits;
    }
}
