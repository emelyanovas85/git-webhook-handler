package ru.cbr.bugbusters.gitwebhookhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.cbr.bugbusters.gitwebhookhandler.common.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class GitlabWebhookHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitlabWebhookHandlerApplication.class, args);
    }
}
