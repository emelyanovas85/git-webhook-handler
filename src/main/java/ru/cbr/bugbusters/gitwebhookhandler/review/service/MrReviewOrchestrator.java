package ru.cbr.bugbusters.gitwebhookhandler.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.cbr.bugbusters.gitwebhookhandler.review.api.ReviewTriggerCommand;
import ru.cbr.bugbusters.gitwebhookhandler.review.domain.GroupReviewResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrReviewOrchestrator {

    private final MrRagContextClient mrRagContextClient;
    private final LlmReviewService llmReviewService;
    private final MarkdownCommentFormatter markdownCommentFormatter;
    private final GitLabNotesPublisher gitLabNotesPublisher;
    @Qualifier("reviewExecutor")
    private final Executor reviewExecutor;

    @Async("reviewExecutor")
    public void runReview(ReviewTriggerCommand command) {
        log.info("Starting async review for project={}, mrIid={}", command.projectId(), command.mrIid());

        List<MrRagContextClient.ReviewGroupContext> contexts = mrRagContextClient.fetchContexts(command);
        if (contexts.isEmpty()) {
            gitLabNotesPublisher.postNote(
                    command.projectId(),
                    command.mrIid(),
                    "**AI Review**: no context found for analysis.");
            return;
        }

        List<CompletableFuture<GroupReviewResult>> futures = contexts.stream()
                .map(ctx -> CompletableFuture.supplyAsync(() -> llmReviewService.review(ctx), reviewExecutor))
                .toList();

        List<GroupReviewResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        String comment = markdownCommentFormatter.format(command, results);
        gitLabNotesPublisher.postNote(command.projectId(), command.mrIid(), comment);
    }
}
