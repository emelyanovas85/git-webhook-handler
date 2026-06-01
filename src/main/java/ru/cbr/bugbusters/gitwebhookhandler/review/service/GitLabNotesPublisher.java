package ru.cbr.bugbusters.gitwebhookhandler.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitLabNotesPublisher {

    private final GitLabApi gitLabApi;

    public void postNote(Long projectId, Long mrIid, String body) {
        try {
            gitLabApi.getNotesApi().createMergeRequestNote(projectId, mrIid, body, null, null);
            log.info("Комментарий с результатами ревью опубликован: project={}, mrIid={}", projectId, mrIid);
        } catch (GitLabApiException e) {
            log.error("Ошибка публикации комментария ревью: project={}, mrIid={}", projectId, mrIid, e);
        }
    }
}
