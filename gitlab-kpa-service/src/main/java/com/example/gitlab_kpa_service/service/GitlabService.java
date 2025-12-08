package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.Projects;
import com.example.gitlab_kpa_service.model.GitLabProjectsDTO;
import com.example.gitlab_kpa_service.repository.ProjectsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitlabService {
    private final ProjectService projectService;
    private final ProjectsRepository projectsRepository;


    private List<Projects> saveProjectsToDb() {
        Flux<GitLabProjectsDTO> projectsFlux = projectService.getProjectsForUser();
        List<Projects> projectsList = projectsFlux.toStream().map(
                project -> {
                    return Projects.builder()
                            .gitlabProjectId(project.getId())
                            .name(project.getName())
                            .pathWithNamespace(project.getPathWithNamespace())
                            .createdAt(project.getCreatedAt())
                            .updatedAt(project.getUpdatedAt())
                            .build();
                }
        ).toList();

        return projectsRepository.saveAll(projectsList);
    }

    private
}
