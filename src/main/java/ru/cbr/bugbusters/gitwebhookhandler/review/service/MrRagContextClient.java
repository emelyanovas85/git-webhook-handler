package ru.cbr.bugbusters.gitwebhookhandler.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.cbr.bugbusters.gitwebhookhandler.common.config.AppProperties;
import ru.cbr.bugbusters.gitwebhookhandler.review.api.ReviewTriggerCommand;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrRagContextClient {

    private final RestClient restClient;
    private final AppProperties appProperties;

    public List<ReviewGroupContext> fetchContexts(ReviewTriggerCommand command) {
        try {
            ReviewGroupContext[] response = restClient.post()
                    .uri(appProperties.review().mrRagUrl() + "/api/review")
                    .body(new MrRagRequest(
                            command.projectId(),
                            command.mrIid(),
                            command.sourceBranch(),
                            command.targetBranch()))
                    .retrieve()
                    .body(ReviewGroupContext[].class);
            return response == null ? List.of() : List.of(response);
        } catch (Exception e) {
            log.error("Failed to fetch contexts from java-mr-rag for MR {}: {}", command.mrIid(), e.getMessage(), e);
            return List.of();
        }
    }

    public record MrRagRequest(Long projectId, Long mrIid, String sourceBranch, String targetBranch) {}

    public record ReviewGroupContext(String groupName, String diffSummary, String enrichedContext) {}
}
