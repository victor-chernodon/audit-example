package com.auditexample.api.audit.client.configs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

  @Bean(name = "auditLogSubmissionPool")
  public ExecutorService auditLogSubmissionExecutor() {
    return Executors.newWorkStealingPool();
  }

}
