package ru.cbr.bugbusters.gitwebhookhandler.webhook.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NoteHookPayload(
        @JsonProperty("object_kind") String objectKind,
        @JsonProperty("event_type") String eventType,
        UserInfo user,
        @JsonProperty("project_id") Long projectId,
        @JsonProperty("object_attributes") NoteAttributes objectAttributes,
        @JsonProperty("merge_request") MergeRequestInfo mergeRequest
) {
}
