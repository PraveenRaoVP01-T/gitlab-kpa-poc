package com.example.gitlab_kpa_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommitData {
    private String commitSha;
    private Integer additions;
    private Integer deletions;
    private Integer netNewLines;
}
