package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "issues")
public class Issues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gitlabIssueId;
    private Long projectId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;

    private String state; // opened, closed, etc.

    @Column(name = "issue_type")
    private String issueType; // bug, feature, unknown classification

    @ElementCollection
    @CollectionTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label")
    private List<String> labels;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant closedAt;
}
