package com.example.gitlab_kpa_service.repository;

import com.example.gitlab_kpa_service.entity.Issues;
import com.example.gitlab_kpa_service.entity.MergeRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuesRepository extends JpaRepository<Issues, Long> {
}
