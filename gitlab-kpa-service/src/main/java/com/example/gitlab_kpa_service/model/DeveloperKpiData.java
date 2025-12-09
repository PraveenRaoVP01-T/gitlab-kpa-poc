package com.example.gitlab_kpa_service.model;

import com.example.gitlab_kpa_service.entity.MergeRequests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeveloperKpiData {
    private String authorName;
    private List<MergeRequestsData> mergeRequests;
    private List<CommitData> commitsData;
}
