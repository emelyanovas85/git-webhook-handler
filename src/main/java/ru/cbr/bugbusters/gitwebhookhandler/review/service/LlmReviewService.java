package ru.cbr.bugbusters.gitwebhookhandler.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import ru.cbr.bugbusters.gitwebhookhandler.common.config.AppProperties;
import ru.cbr.bugbusters.gitwebhookhandler.review.domain.GroupReviewResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmReviewService {

    private final ChatClient chatClient;
    private final AppProperties appProperties;

    public GroupReviewResult review(MrRagContextClient.ReviewGroupContext context) {
        try {
            String response = chatClient.prompt()
                    .system(appProperties.ai().systemPrompt())
                    .user(buildPrompt(context))
                    .call()
                    .content();
            return GroupReviewResult.success(
                    context.groupName(),
                    response == null ? "No issues found." : response);
        } catch (Exception e) {
            log.error("LLM request failed for group {}", context.groupName(), e);
            return GroupReviewResult.failure(context.groupName(), e.getMessage());
        }
    }

    private String buildPrompt(MrRagContextClient.ReviewGroupContext context) {
        return """
                ## Change group: %s

                ### Diff summary:
                %s

                ### Enriched context:
                %s
                """.formatted(context.groupName(), context.diffSummary(), context.enrichedContext());
    }
}
