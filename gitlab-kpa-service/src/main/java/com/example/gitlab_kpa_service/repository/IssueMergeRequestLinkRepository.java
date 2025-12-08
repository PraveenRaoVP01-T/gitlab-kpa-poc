package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.IssueMRLinkId;
import com.example.gitlab_kpa_service.entity.IssueMergeRequestLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueMergeRequestLinkRepository extends JpaRepository<IssueMergeRequestLink, IssueMRLinkId> {
}
