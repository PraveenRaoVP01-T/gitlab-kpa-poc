package com.example.gitlab_kpa_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeRequestsDTO {
    private Long id;
    private Long iid;
    @JsonProperty("project_id")
    private Long projectId;
    private String title;
    private String description;
    private String state;
    @JsonProperty("source_branch")
    private String sourceBranch;
    @JsonProperty("target_branch")
    private String targetBranch;

    private Author author;

    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
    @JsonProperty("merged_at")
    private Instant mergedAt;
    @JsonProperty("closed_at")
    private Instant closedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Author {
        private Long id;
        private String username;
        private String name;
    }

}
