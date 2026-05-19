package ru.cbr.bugbusters.gitwebhookhandler.webhook.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NoteAttributes(
        Long id,
        String note,
        @JsonProperty("noteable_type") String noteableType,
        String url
) {
}
