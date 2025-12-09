package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.IssueMRLinkId;
import com.example.gitlab_kpa_service.entity.IssueMergeRequestLink;
import com.example.gitlab_kpa_service.entity.Issues;
import com.example.gitlab_kpa_service.entity.MergeRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IssueMergeRequestLinkRepository extends JpaRepository<IssueMergeRequestLink, IssueMRLinkId> {
    @Query("SELECT i.issue FROM IssueMergeRequestLink i WHERE i.mergeRequest = :mergeRequests")
    List<Issues> getAllIssuesByMergeRequests(MergeRequests mergeRequests);

    @Query("SELECT i.issue FROM IssueMergeRequestLink i WHERE i.mergeRequest = :mr AND (DATE(i.issue.updatedAt) = DATE(:today) OR DATE(i.issue.closedAt) = DATE(:today1) OR DATE(i.issue.createdAt) = DATE(:today1))")
    List<Issues> getAllIssuesByMergeRequestsAndDateMatch(MergeRequests mr, LocalDate today, LocalDate today1);
}
