package com.example.gitlab_kpa_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommitDiffDTO {
    private String diff;
    @JsonProperty("new_path")
    private String newPath;
}
