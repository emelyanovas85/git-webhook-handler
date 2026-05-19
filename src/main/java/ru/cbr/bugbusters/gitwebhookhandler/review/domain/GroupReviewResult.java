package ru.cbr.bugbusters.gitwebhookhandler.review.domain;

public record GroupReviewResult(
        String groupName,
        String reviewText,
        boolean success
) {
    public static GroupReviewResult success(String groupName, String reviewText) {
        return new GroupReviewResult(groupName, reviewText, true);
    }

    public static GroupReviewResult failure(String groupName, String errorMessage) {
        return new GroupReviewResult(groupName, "Analysis error: " + errorMessage, false);
    }
}
