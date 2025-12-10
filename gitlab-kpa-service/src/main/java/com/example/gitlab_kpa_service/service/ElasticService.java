package com.example.gitlab_kpa_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.example.gitlab_kpa_service.entity.DeveloperActivityDaily;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticService {
    private final ElasticsearchClient client;

    public void indexDeveloperActivities(List<DeveloperActivityDaily> list) throws IOException {
        for (DeveloperActivityDaily activity : list) {
            client.index(i -> i
                    .index("developer-activity-daily")
                    .id(activity.getId().toString())
                    .document(activity)
            );
        }
    }

    public void bulkIndexActivities(List<DeveloperActivityDaily> list) throws IOException {

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (DeveloperActivityDaily act : list) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("developer-activity-daily")
                            .id(act.getId() + "")
                            .document(act)
                    ));
        }

        client.bulk(br.build());
    }
}
