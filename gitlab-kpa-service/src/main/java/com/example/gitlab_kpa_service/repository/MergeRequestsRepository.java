package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.MergeRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MergeRequestsRepository extends JpaRepository<MergeRequests, Long> {
}
