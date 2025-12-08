package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "merge_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergeRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gitlab_mr_id", unique = true, nullable = false)
    private Long gitlabMrId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Projects project;
    @Column(name = "source_branch")
    private String sourceBranch;
    @Column(name = "target_branch")
    private String targetBranch;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String state; // opened, merged, closed
    @Column(name = "author_username")
    private String authorUsername;
    @Column(name = "issue_type")
    private String issueType;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant mergedAt;
}
