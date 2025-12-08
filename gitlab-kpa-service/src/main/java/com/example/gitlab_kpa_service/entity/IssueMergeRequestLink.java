package com.example.gitlab_kpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "issue_merge_request_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(IssueMRLinkId.class)
public class IssueMergeRequestLink {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issues issue;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merge_request_id")
    private MergeRequests mergeRequest;
}

