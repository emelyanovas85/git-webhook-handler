package ru.cbr.bugbusters.gitwebhookhandler.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cbr.bugbusters.gitwebhookhandler.webhook.domain.NoteHookPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitLabWebhookDispatcher {

    private final ObjectMapper objectMapper;
    private final NoteHookHandler noteHookHandler;

    public void dispatch(String eventType, String rawPayload) {
        try {
            if ("Note Hook".equalsIgnoreCase(eventType)) {
                NoteHookPayload payload = objectMapper.readValue(rawPayload, NoteHookPayload.class);
                noteHookHandler.handle(payload);
                return;
            }
            log.debug("Ignoring unsupported GitLab event: {}", eventType);
        } catch (Exception e) {
            log.error("Failed to dispatch GitLab webhook event={}", eventType, e);
        }
    }
}
