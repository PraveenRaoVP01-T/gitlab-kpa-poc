package com.example.gitlab_kpa_service.model;

import com.example.gitlab_kpa_service.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllData {
    private List<Projects> projectsList;
    private List<Issues> issuesList;
    private List<MergeRequests> mergeRequests;
    private List<IssueMergeRequestLink> issueMergeRequestLinks;
    private List<Commit> commits;
    private List<CommitFile> commitFiles;
    private List<CommitStats> commitStats;
}
