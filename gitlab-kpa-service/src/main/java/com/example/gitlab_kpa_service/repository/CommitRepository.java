package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.Commit;
import com.example.gitlab_kpa_service.entity.MergeRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByMergeRequest(MergeRequests request);
}
