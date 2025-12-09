package com.example.gitlab_kpa_service.utils;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
    public static Map<String, Integer> calculateStats(String diffText) {
        int additions = 0;
        int deletions = 0;

        for (String line : diffText.split("\n")) {

            // Skip headers
            if (line.startsWith("---") || line.startsWith("+++") || line.startsWith("@@")) {
                continue;
            }

            if (line.startsWith("+")) {
                additions++;
            } else if (line.startsWith("-")) {
                deletions++;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("additions", additions);
        result.put("deletions", deletions);
        return result;
    }
}
