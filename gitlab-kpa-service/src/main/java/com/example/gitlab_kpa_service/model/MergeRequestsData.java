package com.example.gitlab_kpa_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeRequestsData {
    private Long mrId;
    private Long mrIid;
    private String sourceBranch;
    private String targetBranch;
    private String title;
    private String description;

    private List<CommitData> commitData;

    private List<IssuesData> issuesData;
}
