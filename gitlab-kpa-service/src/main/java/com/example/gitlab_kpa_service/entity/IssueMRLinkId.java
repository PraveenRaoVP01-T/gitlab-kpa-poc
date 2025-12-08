package com.example.gitlab_kpa_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueMRLinkId implements Serializable {
    private Long issue;
    private Long mergeRequest;
}
