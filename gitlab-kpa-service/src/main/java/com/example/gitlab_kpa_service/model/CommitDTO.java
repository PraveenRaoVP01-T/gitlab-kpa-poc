package com.example.gitlab_kpa_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommitDTO {
    private String id;

    private Stats stats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stats {
        private Integer additions;
        private Integer deletions;
        private Integer total;
    }
}

