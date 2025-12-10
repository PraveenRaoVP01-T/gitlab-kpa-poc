package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.Commit;
import com.example.gitlab_kpa_service.entity.MergeRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByMergeRequest(MergeRequests request);

    @Query("SELECT c FROM Commit c WHERE c.authorName = :username and DATE(c.committedAt) = DATE(:today)")
    List<Commit> findByAuthorNameAndDate(String username, LocalDate today);

    @Query("SELECT c FROM Commit c WHERE c.mergeRequest = :mr AND DATE(c.committedAt) = DATE(:today)")
    List<Commit> findByMergeRequestAndDate(MergeRequests mr, LocalDate today);

    @Query("SELECT c FROM Commit c WHERE DATE(c.committedAt) = DATE(:today)")
    List<Commit> findByCommittedAtDate(LocalDate today);
}
