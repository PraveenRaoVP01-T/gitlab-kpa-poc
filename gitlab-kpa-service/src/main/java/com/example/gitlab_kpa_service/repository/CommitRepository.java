package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
}
