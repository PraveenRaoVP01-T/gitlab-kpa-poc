package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.CommitFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitFileRepository extends JpaRepository<CommitFile, Long> {
}
