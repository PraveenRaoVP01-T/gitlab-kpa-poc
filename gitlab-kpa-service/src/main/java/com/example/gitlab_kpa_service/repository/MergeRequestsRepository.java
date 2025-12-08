package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.MergeRequests;
import com.example.gitlab_kpa_service.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MergeRequestsRepository extends JpaRepository<MergeRequests, Long> {
    List<MergeRequests> findByProject(Projects project);
}
