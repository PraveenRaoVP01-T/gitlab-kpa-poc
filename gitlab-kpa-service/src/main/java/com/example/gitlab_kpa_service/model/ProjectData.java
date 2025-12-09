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
public class ProjectData {
    private Long gitlabProjectId;
    private String name;
    private String pathWithNamespace;
    List<DeveloperKpiData> developerKpiDataList;
}
