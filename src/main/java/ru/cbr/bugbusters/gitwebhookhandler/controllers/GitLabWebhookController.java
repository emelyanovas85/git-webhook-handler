package ru.cbr.bugbusters.gitwebhookhandler.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cbr.bugbusters.gitwebhookhandler.common.config.AppProperties;
import ru.cbr.bugbusters.gitwebhookhandler.webhook.service.GitLabWebhookDispatcher;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "GitLab Webhook", description = "Receiving GitLab webhooks")
public class GitLabWebhookController {

    private final GitLabWebhookDispatcher dispatcher;
    private final AppProperties appProperties;

    @Operation(summary = "Accept GitLab webhook", description = "Handles Note Hook to trigger AI review on MR")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Webhook accepted",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Accepted"))),
            @ApiResponse(responseCode = "403", description = "Invalid webhook token")
    })
    @PostMapping("/gitlab")
    public ResponseEntity<String> handleGitLabWebhook(
            @RequestHeader(value = "X-Gitlab-Event", required = false) String eventType,
            @RequestHeader(value = "X-Gitlab-Token", required = false) String token,
            @RequestBody String rawPayload) {

        if (!appProperties.gitlab().webhookToken().equals(token)) {
            log.warn("Rejected GitLab webhook due to invalid token");
            return ResponseEntity.status(403).body("Forbidden");
        }

        log.info("Accepted GitLab webhook. Event={}", eventType);
        dispatcher.dispatch(eventType, rawPayload);
        return ResponseEntity.accepted().body("Accepted");
    }
}
