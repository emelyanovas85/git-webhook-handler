package ru.cbr.bugbusters.gitwebhookhandler.review.api;

public record ReviewTriggerCommand(
        Long projectId,
        Long mrIid,
        String sourceBranch,
        String targetBranch,
        Long triggerNoteId,
        String triggeredBy
) {
}
