package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "developer_activity_daily")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperActivityDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Projects project;

    private String developer;

    @Column(name = "activity_date")
    private LocalDate activityDate;

    @Column(name = "commits_count")
    private Integer commitsCount;

    @Column(name = "mrs_count")
    private Integer mrsCount;

    @Column(name = "lines_added")
    private Integer linesAdded;

    @Column(name = "lines_deleted")
    private Integer linesDeleted;

    @Column(name = "net_new_lines")
    private Integer netNewLines;

    @Column(name = "bug_fix_time_minutes")
    private Integer bugFixTimeMinutes;

    @Column(name = "feature_dev_time_minutes")
    private Integer featureDevTimeMinutes;

    @Column(name = "bug_commits")
    private Integer bugCommits;

    @Column(name = "feature_commits")
    private Integer featureCommits;
}
