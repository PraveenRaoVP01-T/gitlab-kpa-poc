package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "projects")
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gitlab_project_id", unique = true, nullable = false)
    private Long gitlabProjectId;
    private String name;
    @Column(name = "path_with_namespace")
    private String pathWithNamespace;
    private Instant createdAt;
    private Instant updatedAt;
}
