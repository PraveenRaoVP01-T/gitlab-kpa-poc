package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "commits")
public class Commit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gitlab_commit_id", unique = true, nullable = false)
    private String gitlabCommitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merge_request_id")
    private MergeRequests mergeRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Projects project;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_email")
    private String authorEmail;

    @Column(name = "committed_at")
    private Instant committedAt;

    @Column(name = "issue_type")
    private String issueType;

    @Column(columnDefinition = "TEXT")
    private String message;
}
