package com.example.gitlab_kpa_service.utils;

public class ApiEndpoints {
    public final static String GET_ALL_PROJECTS_FOR_USER = "/users/%s/projects";
    public static final String GET_PROJECT_BY_ID = "/projects/%s";
    public static final String GET_ALL_MERGE_REQUESTS_BY_PROJECT_ID = "/projects/%s/merge_requests";
    public static final String GET_ALL_COMMITS_BY_PROJECT_ID = "/projects/%s/repository/commits";
    public static final String GET_COMMIT_DATA_BY_ID = "/projects/%s/repository/commits/%s";
}
