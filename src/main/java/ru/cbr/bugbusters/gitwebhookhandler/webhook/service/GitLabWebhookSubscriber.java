package ru.cbr.bugbusters.gitwebhookhandler.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.cbr.bugbusters.webhookdistributor.client.WebhookClientProperties;
import ru.cbr.bugbusters.webhookdistributor.client.WebhookDeliveryPayload;
import ru.cbr.bugbusters.webhookdistributor.client.WebhookSubscriberClient;

import java.util.Map;

/**
 * Подписчик на webhook-distributor.
 * При старте регистрируется в webhook-distributor через SSE
 * и передаёт полученные события в {@link GitLabWebhookDispatcher}.
 *
 * <p>Настройка (через application.yml):
 * <pre>{@code
 * webhook:
 *   client:
 *     server-url: http://webhook-distributor:8080
 *     subscriber-id: git-webhook-handler
 *     source: gitlab
 *     target-endpoint: gitlab.merge-request
 *     event-type-filter:
 *       - Merge Request Hook
 * }</pre>
 */
@Slf4j
@Component
public class GitLabWebhookSubscriber extends WebhookSubscriberClient {

    private final GitLabWebhookDispatcher dispatcher;
    private final ObjectMapper objectMapper;

    public GitLabWebhookSubscriber(WebhookClientProperties props,
                                   WebClient.Builder webClientBuilder,
                                   ApplicationContext applicationContext,
                                   GitLabWebhookDispatcher dispatcher,
                                   ObjectMapper objectMapper) {
        super(props, webClientBuilder, applicationContext);
        this.dispatcher = dispatcher;
        this.objectMapper = objectMapper;
    }

    /**
     * Вызывается для каждого входящего события через SSE-поток.
     *
     * <p>{@code payload.eventType()} — тип события от GitLab
     *    ("Merge Request Hook", "Push Hook", и т.д.).
     * <p>{@code payload.payload()} — оригинальный JSON от GitLab в виде Map.
     */
    @Override
    protected void handleWebhook(WebhookDeliveryPayload payload) {
        String eventType = payload.eventType();
        Map<String, Object> rawPayload = payload.payload();

        if (rawPayload == null) {
            log.warn("Получен webhook с пустым payload, eventType={}, eventId={}",
                    eventType, payload.eventId());
            return;
        }

        log.info("Получен webhook от дистрибьютора: eventId={}, eventType={}",
                payload.eventId(), eventType);

        try {
            // Сериализуем Map обратно в JSON-строку, чтобы GitLabWebhookDispatcher
            // мог разобрать её через ObjectMapper в нужный domain-объект
            String rawJson = objectMapper.writeValueAsString(rawPayload);
            dispatcher.dispatch(eventType, rawJson);
        } catch (Exception e) {
            log.error("Ошибка обработки webhook eventId={}, eventType={}: {}",
                    payload.eventId(), eventType, e.getMessage(), e);
        }
    }
}
