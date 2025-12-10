package com.example.gitlab_kpa_service.service;

import com.example.gitlab_kpa_service.entity.DeveloperActivityDaily;
import com.example.gitlab_kpa_service.model.AllData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitlabCronJobs {
    private final GitlabService gitlabService;
    private final DeveloperKpiService developerKpiService;
    private final ElasticService elasticService;

    @Scheduled(cron = "0 0 * * * *")
    public AllData syncAllDataAndSendToElasticSearch() {
        return getAllData(LocalDate.now().minusDays(1));
    }

    public AllData getAllData(LocalDate date) {
        if(date == null) {
            date = LocalDate.now().minusDays(1);
        }
        AllData allData = gitlabService.getAllData();
        List<DeveloperActivityDaily> devActivities = developerKpiService.formulateDeveloperActivityDaily(date);

        try {
            elasticService.bulkIndexActivities(devActivities);
        } catch (IOException e) {
            log.error("Error while indexing data to elasticsearch: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e);
        }

        return allData;
    }
}
