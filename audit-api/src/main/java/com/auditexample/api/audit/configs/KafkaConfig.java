package com.auditexample.api.audit.configs;

import com.auditexample.api.audit.properties.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KafkaConfig {

  @Value("${kafka_servers:}")
  private String bootstrapServers;

  @Bean
  public KafkaService kafkaService() {
    log.debug("bootstrap servers: {}", bootstrapServers);
    return new KafkaService(bootstrapServers);
  }

}