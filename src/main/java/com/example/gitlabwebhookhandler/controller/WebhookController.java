package com.example.gitlabwebhookhandler.controller;

import com.example.gitlabwebhookhandler.service.GitLabWebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final GitLabWebhookService gitLabWebhookService;

    /**
     * GitLab webhook endpoint.
     * Header X-Gitlab-Event contains the event type.
     * Header X-Gitlab-Token contains the secret (optional).
     */
    @PostMapping("/gitlab")
    public ResponseEntity<String> handleGitLabWebhook(
            @RequestHeader(value = "X-Gitlab-Event", required = false) String eventType,
            @RequestHeader(value = "X-Gitlab-Token", required = false) String token,
            @RequestBody JsonNode payload) {

        log.info("Received GitLab webhook. Event: {}", eventType);
        gitLabWebhookService.process(eventType, token, payload);
        return ResponseEntity.ok("Webhook processed");
    }
}
