package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IssueMRLinkId implements Serializable {
    @Column(name = "issue_id")
    private Long issueId;
    @Column(name = "merge_request_id")
    private Long mergeRequestId;
}
