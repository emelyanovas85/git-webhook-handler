package ru.cbr.bugbusters.gitwebhookhandler.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("reviewExecutor")
    public Executor reviewExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
