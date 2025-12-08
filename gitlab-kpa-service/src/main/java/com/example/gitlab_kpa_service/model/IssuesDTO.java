package com.example.gitlab_kpa_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssuesDTO {
    private Long id;
    private Long iid;
    @JsonProperty("project_id")
    private Long projectId;
    private String title;
    private String description;
    private String state;

    @JsonProperty("type")
    private String issueType;

    private String severity;

    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
    @JsonProperty("closed_at")
    private Instant closedAt;
    private List<String> labels;
}
