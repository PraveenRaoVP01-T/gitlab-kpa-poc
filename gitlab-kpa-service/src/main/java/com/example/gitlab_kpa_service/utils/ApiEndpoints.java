package com.example.gitlab_kpa_service.utils;

public class ApiEndpoints {
    public static final String GET_ALL_PROJECTS_FOR_USER = "/users/%s/projects";
    public static final String GET_ALL_MERGE_REQUESTS_BY_PROJECT_ID = "/projects/%s/merge_requests";
    public static final String GET_ISSUES_BY_PROJECT_ID = "/projects/%s/issues";
    public static final String GET_RELATED_MR_BY_ISSUE_IID = "/projects/%s/issues/%s/related_merge_requests";
    public static final String GET_COMMITS_BY_MERGE_REQUEST_IID = "/projects/%s/merge_requests/%s/commits";
    public static final String GET_COMMIT_DATA_BY_ID = "/projects/%s/repository/commits/%s";
}
