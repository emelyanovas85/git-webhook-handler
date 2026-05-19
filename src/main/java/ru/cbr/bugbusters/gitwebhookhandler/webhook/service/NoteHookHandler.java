package ru.cbr.bugbusters.gitwebhookhandler.webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cbr.bugbusters.gitwebhookhandler.common.config.AppProperties;
import ru.cbr.bugbusters.gitwebhookhandler.review.api.ReviewTriggerCommand;
import ru.cbr.bugbusters.gitwebhookhandler.review.persistence.ReviewEventEntity;
import ru.cbr.bugbusters.gitwebhookhandler.review.persistence.ReviewEventRepository;
import ru.cbr.bugbusters.gitwebhookhandler.review.service.MrReviewOrchestrator;
import ru.cbr.bugbusters.gitwebhookhandler.webhook.domain.NoteHookPayload;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteHookHandler {

    private final AppProperties appProperties;
    private final ReviewEventRepository reviewEventRepository;
    private final MrReviewOrchestrator mrReviewOrchestrator;

    public void handle(NoteHookPayload payload) {
        if (payload.objectAttributes() == null || payload.mergeRequest() == null) {
            log.debug("Skipping note hook without merge request payload");
            return;
        }
        if (!"MergeRequest".equals(payload.objectAttributes().noteableType())) {
            log.debug("Skipping note hook for noteableType={}", payload.objectAttributes().noteableType());
            return;
        }
        String trigger = payload.objectAttributes().note();
        if (trigger == null || !trigger.trim().equalsIgnoreCase(appProperties.review().triggerCommand())) {
            log.debug("Skipping note '{}' — not a trigger command", trigger);
            return;
        }

        Long projectId = payload.projectId();
        Long mrIid = payload.mergeRequest().iid();
        Long noteId = payload.objectAttributes().id();

        if (reviewEventRepository.existsByProjectIdAndMrIidAndNoteId(projectId, mrIid, noteId)) {
            log.info("Duplicate review trigger ignored for project={}, mrIid={}, noteId={}", projectId, mrIid, noteId);
            return;
        }

        ReviewEventEntity entity = new ReviewEventEntity();
        entity.setProjectId(projectId);
        entity.setMrIid(mrIid);
        entity.setNoteId(noteId);
        entity.setStatus("PENDING");
        entity.setCreatedAt(LocalDateTime.now());
        reviewEventRepository.save(entity);

        mrReviewOrchestrator.runReview(new ReviewTriggerCommand(
                projectId,
                mrIid,
                payload.mergeRequest().sourceBranch(),
                payload.mergeRequest().targetBranch(),
                noteId,
                payload.user() != null ? payload.user().username() : "unknown"
        ));
    }
}
