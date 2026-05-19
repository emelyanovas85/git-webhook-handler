package ru.cbr.bugbusters.gitwebhookhandler.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        GitLabProperties gitlab,
        ReviewProperties review,
        AiProperties ai
) {
    public record GitLabProperties(String url, String token, String webhookToken) {}
    public record ReviewProperties(String triggerCommand, String mrRagUrl) {}
    public record AiProperties(String systemPrompt) {}
}
