package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.*;
import com.example.gitlab_kpa_service.model.*;
import com.example.gitlab_kpa_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeveloperKpiService {
    private final CommitRepository commitRepository;
    private final CommitFileRepository commitFileRepository;
    private final CommitStatsRepository commitStatsRepository;
    private final MergeRequestsRepository mergeRequestsRepository;
    private final IssueMergeRequestLinkRepository issueMergeRequestLinkRepository;
    private final IssuesRepository issuesRepository;
    private final ProjectsRepository projectsRepository;
    private final DeveloperActivityDailyRepository developerActivityDailyRepository;

    public List<ProjectData> getBasicDeveloperKpis() {
        List<Projects> projects = projectsRepository.findAll();

        List<ProjectData> projectsData = new ArrayList<>();

        for(Projects project : projects) {
            List<MergeRequests> mergeRequests = mergeRequestsRepository.findByProject(project);

            Map<String, List<MergeRequests>> groupedByContributorsMap =
                    mergeRequests.stream().collect(Collectors.groupingBy(MergeRequests::getAuthorUsername));
            List<DeveloperKpiData> developerKpiData = new ArrayList<>();
            for(Map.Entry<String, List<MergeRequests>> entry: groupedByContributorsMap.entrySet()) {
                List<MergeRequestsData> contributorMrs = new ArrayList<>();
                List<CommitData> contributorCommits = new ArrayList<>();
                List<IssuesData> issuesData = new ArrayList<>();
                for(MergeRequests requests : entry.getValue()) {
                    List<Commit> commits = commitRepository.findByMergeRequest(requests);
                    List<CommitData> commitData = commits.stream()
                            .map(commit -> CommitData.builder()
                                    .commitSha(commit.getGitlabCommitId())
                                    .additions(commit.getCommitStats().getLinesAdded())
                                    .deletions(commit.getCommitStats().getLinesDeleted())
                                    .netNewLines(commit.getCommitStats().getNetNewLines())
                                    .build()).toList();
                    MergeRequestsData requestsData = MergeRequestsData.builder()
                            .mrId(requests.getGitlabMrId())
                            .mrIid(requests.getGitlabMrIID())
                            .sourceBranch(requests.getSourceBranch())
                            .targetBranch(requests.getTargetBranch())
                            .title(requests.getTitle())
                            .description(requests.getDescription())
                            .commitData(commitData)
                            .build();
                    contributorMrs.add(requestsData);
                    contributorCommits.addAll(commitData);

                    List<Issues> issues = issueMergeRequestLinkRepository.getAllIssuesByMergeRequests(requests);
                    if(!issues.isEmpty()) {
                        List<IssuesData> issueForMergeRequest = issues.stream().map(
                                issue -> IssuesData.builder()
                                        .issueId(issue.getGitlabIssueId())
                                        .title(issue.getTitle())
                                        .description(issue.getDescription())
                                        .build()
                        ).toList();
                        issuesData.addAll(issueForMergeRequest);
                    }
                }
                DeveloperKpiData devData = DeveloperKpiData.builder()
                        .authorName(entry.getKey())
                        .mergeRequests(contributorMrs)
                        .commitsData(contributorCommits)
                        .build();
                developerKpiData.add(devData);
            }

            ProjectData projData = ProjectData.builder()
                    .gitlabProjectId(project.getGitlabProjectId())
                    .name(project.getName())
                    .pathWithNamespace(project.getPathWithNamespace())
                    .developerKpiDataList(developerKpiData)
                    .build();
            projectsData.add(projData);
        }

        return projectsData;

    }


    public List<ProjectData> getTodayDeveloperKpiByUsername(String username, LocalDate date) {

        LocalDate today = date != null ? date : LocalDate.now();
        List<Commit> commits = commitRepository.findByAuthorNameAndDate(username, today);
        if (commits.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Collect MR IDs (some commits may not belong to an MR if pushed directly)
        Set<MergeRequests> mergeRequests = commits.stream()
                .map(Commit::getMergeRequest)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. Group commits by project
        Map<Projects, List<Commit>> commitsByProject = commits.stream()
                .collect(Collectors.groupingBy(Commit::getProject));

        List<ProjectData> result = new ArrayList<>();

        // 4. For each project, build ProjectData
        for (Map.Entry<Projects, List<Commit>> entry : commitsByProject.entrySet()) {
            Projects project = entry.getKey();
            List<Commit> projectCommits = entry.getValue();

            // Filter MRs belonging to this project
            List<MergeRequests> projectMrs = mergeRequests.stream()
                    .filter(mr -> mr.getProject().equals(project))
                    .toList();

            // 5. Build DeveloperKpiData
            DeveloperKpiData developerData = DeveloperKpiData.builder()
                    .authorName(username)
                    .mergeRequests(buildMRData(projectMrs,today))
                    .commitsData(buildCommitData(projectCommits))
                    .build();

            // 6. Build ProjectData
            ProjectData projectData = ProjectData.builder()
                    .gitlabProjectId(project.getGitlabProjectId())
                    .name(project.getName())
                    .pathWithNamespace(project.getPathWithNamespace())
                    .developerKpiDataList(List.of(developerData))
                    .build();

            result.add(projectData);
        }

        return result;

    }

    private List<CommitData> buildCommitData(List<Commit> commits) {

        return commits.stream()
                .map(commit -> {
                    CommitStats stats = commit.getCommitStats();

                    return CommitData.builder()
                            .commitSha(commit.getGitlabCommitId())
                            .additions(stats != null ? stats.getLinesAdded() : 0)
                            .deletions(stats != null ? stats.getLinesDeleted() : 0)
                            .netNewLines(stats != null ? stats.getNetNewLines() : 0)
                            .build();
                })
                .toList();
    }

    private List<MergeRequestsData> buildMRData(List<MergeRequests> mergeRequests, LocalDate today) {

        return mergeRequests.stream()
                .map(mr -> {
                    // Fetch commits under this MR
                    List<Commit> mrCommits = commitRepository.findByMergeRequestAndDate(mr, today);

                    // Fetch issues under this MR
                    List<Issues> issues = issueMergeRequestLinkRepository.getAllIssuesByMergeRequestsAndDateMatch(mr, today, today);

                    return MergeRequestsData.builder()
                            .mrId(mr.getId())
                            .mrIid(mr.getGitlabMrIID())
                            .sourceBranch(mr.getSourceBranch())
                            .targetBranch(mr.getTargetBranch())
                            .title(mr.getTitle())
                            .description(mr.getDescription())
                            .commitData(buildCommitData(mrCommits))
                            .issuesData(buildIssuesData(issues))
                            .build();
                })
                .toList();
    }

    private List<IssuesData> buildIssuesData(List<Issues> issues) {
        return issues.stream()
                .map(issue -> IssuesData.builder()
                        .issueId(issue.getGitlabIssueId())
                        .title(issue.getTitle())
                        .description(issue.getDescription())
                        .build()
                )
                .toList();
    }
}
