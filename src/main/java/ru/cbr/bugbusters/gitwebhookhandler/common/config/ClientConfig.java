package ru.cbr.bugbusters.gitwebhookhandler.common.config;

import org.gitlab4j.api.GitLabApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public GitLabApi gitLabApi(AppProperties properties) {
        return new GitLabApi(properties.gitlab().url(), properties.gitlab().token());
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    /**
     * @Lazy — бин создаётся только при первом обращении.
     * Позволяет приложению стартовать без реального OPENAI_API_KEY
     * (например, в CI/CD без LLM или при локальном тестировании).
     * При реальном вызове review OPENAI_API_KEY должен быть задан через env.
     */
    @Bean
    @Lazy
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
