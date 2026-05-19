package ru.cbr.bugbusters.gitwebhookhandler.webhook.domain;

public record UserInfo(
        Long id,
        String name,
        String username
) {
}
