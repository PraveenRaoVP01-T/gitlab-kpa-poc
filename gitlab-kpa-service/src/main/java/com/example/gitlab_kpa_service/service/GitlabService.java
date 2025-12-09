package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.*;
import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.repository.*;
import com.example.gitlab_kpa_service.utils.CommonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitlabService {
    private final ProjectService projectService;
    private final ProjectsRepository projectsRepository;

    private final MergeRequestsRepository mergeRequestsRepository;

    private final IssuesRepository issuesRepository;

    private final IssueMergeRequestLinkRepository issueMergeRequestLinkRepository;

    private final CommitRepository commitRepository;

    private final CommitStatsRepository commitStatsRepository;
    private final CommitFileRepository commitFileRepository;


    private List<Projects> saveProjectsToDb() {
        Flux<GitLabProjectsDTO> projectsFlux = projectService.getProjectsForUser();
        List<Projects> existingProjects = projectsRepository.findAll();
        List<Projects> projectsList = projectsFlux.toStream().map(
                project -> Projects.builder()
                        .gitlabProjectId(project.getId())
                        .name(project.getName())
                        .pathWithNamespace(project.getPathWithNamespace())
                        .createdAt(project.getCreatedAt())
                        .updatedAt(project.getUpdatedAt())
                        .build()
        ).toList();

        projectsList = projectsList.stream()
                .filter(project -> existingProjects.stream().noneMatch(ep -> ep.getGitlabProjectId().equals(project.getGitlabProjectId()))).toList();


        projectsRepository.saveAll(projectsList);
        return projectsRepository.findAll();
    }

    private List<MergeRequests> saveMergeRequestsToDb(List<Projects> projects) {
        List<MergeRequests> mergeRequests = new ArrayList<>();
        for (Projects project : projects) {
            List<MergeRequests> existingRequests = mergeRequestsRepository.findByProject(project);
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

            mergeRequestsToSave = mergeRequestsToSave.stream()
                            .filter(mr -> existingRequests.stream().noneMatch(er -> er.getGitlabMrId().equals(mr.getGitlabMrId()))).toList();

            mergeRequests.addAll(mergeRequestsToSave);
        }
        mergeRequestsRepository.saveAll(mergeRequests);
        return mergeRequestsRepository.findAll();
    }

    private List<Issues> saveIssuesToDb(List<Projects> projects) {
        List<Issues> issues = new ArrayList<>();
        for (Projects project : projects) {
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

        issuesRepository.saveAll(issues);
        return issuesRepository.findAll();
    }

    @Transactional
    private List<IssueMergeRequestLink> saveIssueMergeRequests(List<Issues> issues) {
        List<IssueMergeRequestLink> issueMergeRequestLinks = new ArrayList<>();

        for (Issues issue : issues) {
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

        issueMergeRequestLinkRepository.saveAll(issueMergeRequestLinks);
        return issueMergeRequestLinkRepository.findAll();
    }

    @Transactional
    private List<Commit> saveCommits(List<MergeRequests> mergeRequests) {

        List<Commit> allNewCommits = new ArrayList<>();

        for (MergeRequests request : mergeRequests) {

            List<Commit> existingCommits = commitRepository.findByMergeRequest(request);

            List<CommitListDTO> incomingCommits = projectService
                    .getCommitsByMergeRequest(
                            request.getProject().getGitlabProjectId(),
                            request.getGitlabMrIID())
                    .toStream()
                    .toList();

            List<CommitListDTO> newCommitDtos = incomingCommits.stream()
                    .filter(dto -> existingCommits.stream()
                            .noneMatch(db -> db.getGitlabCommitId().equals(dto.getId())))
                    .toList();

            List<Commit> commitsToSave = newCommitDtos.stream()
                    .map(dto -> Commit.builder()
                            .gitlabCommitId(dto.getId())
                            .mergeRequest(request)
                            .project(request.getProject())
                            .authorName(dto.getAuthorName())
                            .authorEmail(dto.getAuthorEmail())
                            .committedAt(dto.getCommittedAt())
                            .title(dto.getTitle())
                            .message(dto.getMessage())
                            .build()
                    )
                    .toList();

            allNewCommits.addAll(commitsToSave);
        }

        List<Commit> savedCommits = commitRepository.saveAll(allNewCommits);

        List<CommitStats> allStats = new ArrayList<>();
        List<CommitFile> allFiles = new ArrayList<>();

        for (Commit commit : savedCommits) {

            Long projectId = commit.getProject().getGitlabProjectId();
            String commitId = commit.getGitlabCommitId();

            CommitDTO commitDTO = projectService.getCommitDataById(projectId, commitId).block();
            if (commitDTO != null) {
                CommitStats stats = CommitStats.builder()
                        .commit(commit)
                        .linesAdded(commitDTO.getStats().getAdditions())
                        .linesDeleted(commitDTO.getStats().getDeletions())
                        .netNewLines(commitDTO.getStats().getAdditions() - commitDTO.getStats().getDeletions())
                        .build();

                allStats.add(stats);
            }

            List<CommitDiffDTO> diffs = projectService.getCommitDiffsById(projectId, commitId)
                    .toStream()
                    .toList();

            List<CommitFile> files = diffs.stream().map(diff -> {
                Map<String, Integer> computed = CommonUtils.calculateStats(diff.getDiff());

                return CommitFile.builder()
                        .commit(commit)
                        .filePath(diff.getNewPath())
                        .additions(computed.getOrDefault("additions", 0))
                        .deletions(computed.getOrDefault("deletions", 0))
                        .build();
            }).toList();

            allFiles.addAll(files);
        }

        commitStatsRepository.saveAll(allStats);
        commitFileRepository.saveAll(allFiles);

        return commitRepository.findAll();
    }

    public AllData getAllData() {
        List<Projects> projects = saveProjectsToDb();
        List<MergeRequests> mergeRequests = saveMergeRequestsToDb(projects);
        List<Issues> issues = saveIssuesToDb(projects);
        List<IssueMergeRequestLink> issueMergeRequestLinks = saveIssueMergeRequests(issues);
        List<Commit> commits = saveCommits(mergeRequests);
        return AllData.builder()
                .projectsList(projects)
                .mergeRequests(mergeRequests)
                .issuesList(issues)
                .issueMergeRequestLinks(issueMergeRequestLinks)
                .commits(commits)
                .build();
    }
}
