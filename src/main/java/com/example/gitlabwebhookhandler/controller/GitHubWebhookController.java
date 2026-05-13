package com.example.gitlabwebhookhandler.controller;

import com.example.gitlabwebhookhandler.service.GitHubWebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final GitHubWebhookService gitHubWebhookService;

    /**
     * GitHub webhook endpoint.
     * Header X-GitHub-Event contains the event type.
     * Header X-Hub-Signature-256 contains HMAC-SHA256 signature (optional but recommended).
     * Raw body is required for signature verification.
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String rawBody,
            @RequestBody(required = false) JsonNode payload) {

        log.info("Received GitHub webhook. Event: {}", eventType);

        // Parse payload manually to also keep rawBody for signature verification
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        JsonNode parsedPayload;
        try {
            parsedPayload = mapper.readTree(rawBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid JSON payload");
        }

        gitHubWebhookService.process(eventType, signature, rawBody, parsedPayload);
        return ResponseEntity.ok("Webhook processed");
    }
}
